# Directive: Voice Advisory Pipeline

## Objective

Process a farmer's voice message → transcribe → generate agricultural advisory via RAG → synthesize audio response → send back via Telegram.

## Inputs

- Voice note (OGG/OPUS) from Telegram
- VoiceMessage metadata (chatId, userId, languageCode)

## Execution Flow

1. **Download audio** → `KisanVoiceBot.downloadVoiceFile(fileId)` → raw OGG bytes
2. **Convert format** → `AudioFileService.convertOggToWav(oggBytes)` → WAV 16kHz mono
3. **Transcribe** → `SpeechToTextService.transcribe(wavBytes, languageHint)` → `TranscriptionResult{text, language, confidence}`
4. **Generate advisory** → `AdvisoryService.generateAdvisory(FarmerQuery)`:
   - Vector search on KCC + soil data (top-5, threshold 0.7)
   - LLM prompt with multilingual system instruction + context
   - Cached by query text (1 hour TTL)
5. **Synthesize audio** → `TextToSpeechService.synthesize(text, language)` → `AudioResult{audioBytes}`
6. **Convert to OGG** → `AudioFileService.convertWavToOgg(wavBytes)` → OGG bytes
7. **Send response** → `KisanVoiceBot.sendTextReply()` + `sendVoiceReply()`

## Outputs

- Text advisory message (sent to Telegram chat)
- Audio advisory (OGG voice note, sent to Telegram chat)

## Edge Cases

- **STT fails** → Log error, send "please try again" message
- **No relevant documents in vector store** → LLM generates general advice + suggests contacting KVK
- **TTS fails** → Send text-only advisory (graceful degradation)
- **Voice note > 60s** → Reject with guidance message
- **LLM timeout** → Return cached fallback message in farmer's language
- **Bhashini API rate limit** → Spring Retry (3 attempts, exponential backoff)

## Acceptance Criteria

- [ ] Farmer sends voice → receives text + audio advisory within 15 seconds (p95)
- [ ] Supports at least 5 Indian languages (hi, te, kn, ta, bn)
- [ ] Fallback messages work when AI services are unavailable
- [ ] Repeated similar queries hit cache
