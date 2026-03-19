<p align="center">
  <img src="docs/banner.png" alt="KisanConnect Banner" width="100%"/>
</p>

<h1 align="center">🌾 KisanConnect</h1>

<p align="center">
  <b>AI-Powered Voice Agricultural Advisory Bot for Indian Farmers</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring_AI-1.0.0-6DB33F?logo=spring&logoColor=white" alt="Spring AI"/>
  <img src="https://img.shields.io/badge/Groq-LPU_Inference-F55036?logo=groq&logoColor=white" alt="Groq"/>
  <img src="https://img.shields.io/badge/Telegram-Bot_API-26A5E4?logo=telegram&logoColor=white" alt="Telegram"/>
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License"/>
</p>

<p align="center">
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-how-it-works">How It Works</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-setup-guide">Setup Guide</a> •
  <a href="#-troubleshooting">Troubleshooting</a>
</p>

---

## 🌱 What is KisanConnect?

**KisanConnect** is a voice-driven Telegram bot that delivers **instant agricultural advice** to Indian farmers — in their own language. A farmer simply sends a voice message asking about crop diseases, pest control, fertilizers, or soil management, and the bot responds with **practical, actionable advice** in text.

> **The Problem:** 700+ million Indian farmers need timely agricultural guidance, but most advisory services require literacy, internet browsing, or expensive phone calls. Language barriers make things worse — India has 22+ official languages.

> **The Solution:** KisanConnect lets farmers **speak naturally** in their regional language (Hindi, Marathi, Telugu, Tamil, Bengali, etc.). The AI understands their question, retrieves relevant agricultural data, and responds in the **same language** they spoke in.

### ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🎤 **Voice Input** | Send a voice message in any Indian language — no typing needed |
| 🌐 **Multilingual** | Auto-detects language via Whisper and responds in the same language |
| 🧠 **RAG-Powered** | Retrieves real KCC (Kisan Call Centre) data for grounded, accurate answers |
| ⚡ **Blazing Fast** | Groq LPU inference — responses in 2-3 seconds |
| 💰 **100% Free APIs** | Uses Groq (free tier) for both STT + LLM — no paid subscriptions |
| 📋 **Practical Advice** | Specific pesticide names, dosages, timings — not generic essays |

---

## 🔄 How It Works

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌─────────────┐
│   Farmer     │────▶│  Telegram    │────▶│  Spring Boot │────▶│  Response   │
│  🎤 Voice    │     │  Bot API     │     │  Pipeline    │     │  📋 Text    │
│  Message     │     │              │     │              │     │  Advisory   │
└─────────────┘     └──────────────┘     └──────────────┘     └─────────────┘
```

### Pipeline Flow

```
Voice Message (OGG/OPUS)
    │
    ▼
┌─────────────────────────────────────┐
│  1️⃣  SPEECH-TO-TEXT (Groq Whisper)  │  Transcribes audio + detects language
│     Model: whisper-large-v3-turbo   │  Supports 90+ languages
└──────────────┬──────────────────────┘
               │  "मेरे धान में कीट लग रहे हैं" (Hindi detected)
               ▼
┌─────────────────────────────────────┐
│  2️⃣  RAG RETRIEVAL (Vector Store)   │  Searches KCC agricultural database
│     ONNX Embeddings (local)         │  Finds relevant Q&A documents
└──────────────┬──────────────────────┘
               │  Context: "For rice pest control, use..."
               ▼
┌─────────────────────────────────────┐
│  3️⃣  LLM ADVISORY (Groq Llama 3.1) │  Generates farmer-friendly advice
│     Model: llama-3.1-8b-instant     │  Responds in detected language
└──────────────┬──────────────────────┘
               │  "चावल में कीट नाशक के लिए..."
               ▼
┌─────────────────────────────────────┐
│  4️⃣  TELEGRAM RESPONSE              │  Sends text advisory back to farmer
│     📋 Text reply in same language   │
└─────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Core Platform

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Java 21 LTS | Records, sealed classes, virtual threads |
| **Framework** | Spring Boot 3.4.5 | Application framework |
| **AI Framework** | Spring AI 1.0.0 | LLM integration, vector store, embeddings |
| **Build** | Maven Wrapper | No local Maven install needed |

### AI / ML Models

| Component | Provider | Model | Cost |
|-----------|----------|-------|------|
| 🎤 **Speech-to-Text** | Groq | `whisper-large-v3-turbo` | ✅ Free |
| 🧠 **LLM (Advisory)** | Groq | `llama-3.1-8b-instant` | ✅ Free |
| 📐 **Embeddings** | Local ONNX | Transformers (all-MiniLM-L6-v2) | ✅ Free (runs locally) |
| 🔊 **Text-to-Speech** | HuggingFace | `ai4bharat/indic-parler-tts` | ✅ Free |

### Infrastructure

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Bot Platform** | Telegram Bot API | User interface for farmers |
| **Vector Store** | SimpleVectorStore (in-memory) | RAG document storage (dev) |
| **Database** | H2 (dev) / PostgreSQL (prod) | Persistence |
| **Caching** | Caffeine | Advisory response caching |
| **API Docs** | SpringDoc OpenAPI 3 | Swagger UI at `/swagger-ui.html` |
| **Resilience** | Spring Retry | Auto-retry on API failures |
| **Observability** | Micrometer + Actuator | Health checks, metrics |

---

## 🚀 Quick Start

### Prerequisites

| Requirement | Version | How to get |
|-------------|---------|-----------|
| **Java JDK** | 21+ | [Download Temurin](https://adoptium.net/) |
| **Groq API Key** | Free | [console.groq.com](https://console.groq.com) → API Keys |
| **HuggingFace Token** | Free | [huggingface.co/settings/tokens](https://huggingface.co/settings/tokens) |
| **Telegram Bot Token** | Free | Talk to [@BotFather](https://t.me/BotFather) on Telegram |

### 3-Step Setup

```bash
# 1. Clone the repository
git clone https://github.com/your-username/KisanConnect.git
cd KisanConnect

# 2. Configure environment
cp .env.example .env
# Edit .env with your API keys (see Configuration section below)

# 3. Run the application
# Windows (PowerShell):
powershell -ExecutionPolicy Bypass -File run-dev.ps1

# macOS/Linux:
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

> ⏳ **First run** takes ~60 seconds — the ONNX embedding model downloads and caches automatically.

Once you see `Started KisanConnectApplication` in the logs, your bot is live! 🎉

---

## ⚙️ Setup Guide

### Step 1: Get Your API Keys

#### 🤖 Groq API Key (STT + LLM)

1. Go to [console.groq.com](https://console.groq.com)
2. Sign up with Google/GitHub (free)
3. Navigate to **API Keys** → **Create API Key**
4. Copy the key starting with `gsk_...`

#### 🤗 HuggingFace Token (TTS)

1. Go to [huggingface.co](https://huggingface.co) → Sign up (free)
2. Go to **Settings** → **Access Tokens** → **New token**
3. Select **Read** access → Create
4. Copy the token starting with `hf_...`

#### 📱 Telegram Bot Token

1. Open Telegram → Search for **@BotFather**
2. Send `/newbot`
3. Choose a **name** (e.g., `Kisan Voice Bot`)
4. Choose a **username** (e.g., `KisanVoiceBot`) — must end with `bot`
5. Copy the token BotFather gives you

### Step 2: Configure Environment

Create a `.env` file in the project root:

```env
# Groq API (FREE — powers both STT and LLM)
GROQ_API_KEY=gsk_your_groq_api_key_here

# HuggingFace (FREE — powers TTS)
HUGGINGFACE_API_KEY=hf_your_huggingface_token_here

# Telegram Bot
TELEGRAM_BOT_TOKEN=your_telegram_bot_token_here
TELEGRAM_BOT_USERNAME=YourBotUsername

# Spring Profile
SPRING_PROFILES_ACTIVE=dev
```

> ⚠️ **Never commit `.env` to Git** — it's already in `.gitignore`.

### Step 3: Run the Application

```powershell
# Windows
powershell -ExecutionPolicy Bypass -File run-dev.ps1
```

```bash
# macOS / Linux
export $(cat .env | xargs) && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 4: Verify Everything Works

Open your browser and test these endpoints:

| URL | Expected Result |
|-----|----------------|
| [`localhost:8080/actuator/health`](http://localhost:8080/actuator/health) | `{"status":"UP"}` |
| [`localhost:8080/api/diagnostics/health`](http://localhost:8080/api/diagnostics/health) | `{"llm":"✅ OK","vectorStore":"✅ OK"}` |
| [`localhost:8080/api/diagnostics/llm?prompt=Hello`](http://localhost:8080/api/diagnostics/llm?prompt=Hello) | LLM response with latency |
| [`localhost:8080/api/diagnostics/vectorstore?query=rice+pest`](http://localhost:8080/api/diagnostics/vectorstore?query=rice+pest) | Found KCC documents |
| [`localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html) | API documentation |

---

## 📱 Using the Telegram Bot

### Getting Started

1. Open Telegram on your phone or desktop
2. Search for your bot username (e.g., `@KisanVoiceBot`)
3. Tap **Start** or send `/start`
4. You'll see a welcome message:

```
🌾 Welcome to KisanConnect!
Send a voice message with your agriculture question.
```

### Sending a Voice Query

1. **Hold the microphone button** in Telegram
2. **Speak your question** in any Indian language:
   - 🇮🇳 Hindi: *"मेरे गेहूं में पीला रोग लग रहा है, क्या करूं?"*
   - 🇮🇳 Marathi: *"कापसावर बोंड अळी आली आहे, उपाय सांगा"*
   - 🇮🇳 Telugu: *"వరి పంటలో తెగుళ్ళు వచ్చాయి, ఏం చేయాలి?"*
   - 🇮🇳 Tamil: *"நெல் பயிரில் பூச்சி தாக்கம் உள்ளது"*
3. **Release** to send
4. Wait 3-5 seconds for the response

### Supported Languages

| Language | Code | Language | Code |
|----------|------|----------|------|
| Hindi | `hi` | Gujarati | `gu` |
| Bengali | `bn` | Odia | `or` |
| Telugu | `te` | Punjabi | `pa` |
| Tamil | `ta` | Urdu | `ur` |
| Kannada | `kn` | Assamese | `as` |
| Malayalam | `ml` | English | `en` |
| Marathi | `mr` | Nepali | `ne` |

---

## 📁 Project Structure

```
KisanConnect/
├── 📄 pom.xml                          # Maven dependencies
├── 📄 run-dev.ps1                      # Dev run script (loads .env)
├── 📄 .env.example                     # Environment template
├── 📄 .env                             # Your API keys (gitignored)
│
├── src/main/java/com/kisanconnect/
│   ├── 🚀 KisanConnectApplication.java # App entry point
│   │
│   ├── advisory/                       # 🧠 Core advisory logic
│   │   ├── AdvisoryOrchestrator.java   #   Pipeline coordinator
│   │   ├── AdvisoryService.java        #   RAG + LLM advisory generation
│   │   └── dto/FarmerQuery.java        #   Query record
│   │
│   ├── config/                         # ⚙️ Configuration
│   │   ├── AiConfig.java               #   ChatClient bean
│   │   ├── DiagnosticsController.java  #   Health check endpoints
│   │   ├── VectorStoreConfig.java      #   SimpleVectorStore setup
│   │   └── WebConfig.java              #   CORS configuration
│   │
│   ├── knowledge/                      # 📚 RAG data
│   │   └── SampleDataSeeder.java       #   Seeds 12 KCC Q&A documents
│   │
│   ├── speech/                         # 🎤 Speech services
│   │   ├── HuggingFaceSttService.java  #   Groq Whisper STT
│   │   ├── HuggingFaceTtsService.java  #   HuggingFace TTS
│   │   ├── SpeechToTextService.java    #   STT interface
│   │   ├── TextToSpeechService.java    #   TTS interface
│   │   └── dto/                        #   TranscriptionResult, AudioResult
│   │
│   ├── telegram/                       # 📱 Telegram integration
│   │   ├── KisanVoiceBot.java          #   Main bot handler
│   │   ├── TelegramBotConfig.java      #   Bot registration
│   │   ├── AudioFileService.java       #   Audio format conversion
│   │   └── dto/                        #   AdvisoryResponse, VoiceMessage
│   │
│   └── common/exception/              # 🛡️ Error handling
│       ├── GlobalExceptionHandler.java #   RFC 7807 ProblemDetail
│       └── ResourceNotFoundException.java
│
├── src/main/resources/
│   ├── application.yml                 # Base config
│   └── application-dev.yml             # Dev profile (H2, debug logs)
│
└── directives/                         # 📋 SOPs and documentation
    ├── README.md
    └── voice_advisory_pipeline.md
```

---

## 🔧 Configuration Reference

### `application.yml` — Key Properties

```yaml
# LLM Provider (Groq — free OpenAI-compatible API)
spring.ai.openai:
  base-url: https://api.groq.com/openai
  api-key: ${GROQ_API_KEY}
  chat.options:
    model: llama-3.1-8b-instant    # Fast, free, multilingual
    temperature: 0.7                # Creativity level (0.0–1.0)
    max-tokens: 512                 # Response length cap

# Telegram Bot
kisanconnect.telegram:
  bot-token: ${TELEGRAM_BOT_TOKEN}
  bot-username: KisanVoiceBot

# Speech Models
kisanconnect.huggingface:
  stt-model: whisper-large-v3-turbo  # Groq Whisper (STT)
  tts-model: ai4bharat/indic-parler-tts  # HuggingFace (TTS)
```

### Profiles

| Profile | Database | Vector Store | Use Case |
|---------|----------|-------------|----------|
| `dev` | H2 (in-memory) | SimpleVectorStore | Local development |
| `prod` | PostgreSQL | pgvector | Production deployment |

---

## 🐛 Troubleshooting

### Common Issues

<details>
<summary><b>❌ App fails to start — "Port 8080 already in use"</b></summary>

Another process is using port 8080. Kill it:
```powershell
# Windows
Get-Process -Name java | Stop-Process -Force
# or change the port in application.yml:
# server.port: 8081
```
</details>

<details>
<summary><b>❌ Groq API — "HTTP 413 Request too large"</b></summary>

Groq free tier has a 6,000 TPM (tokens per minute) limit. The app is already optimized for this, but if you hit limits:
- Wait 60 seconds between requests
- The advisory service caps context at 1,500 chars and response at 512 tokens
- Consider upgrading to Groq Dev Tier ($0 — just requires credit card on file)
</details>

<details>
<summary><b>❌ Telegram bot not responding</b></summary>

1. Check if the app is running: `http://localhost:8080/actuator/health`
2. Verify your `TELEGRAM_BOT_TOKEN` is correct in `.env`
3. Make sure no other instance of the bot is running (only one connection per token)
4. Restart the app — Telegram sometimes needs a clean reconnect
</details>

<details>
<summary><b>❌ STT returns empty or wrong text</b></summary>

- Speak clearly and close to the microphone
- Ensure voice messages are at least 2-3 seconds long
- The first request may take 10-15 seconds (Groq Whisper cold start)
- Check logs for STT errors: `log.info("📝 STT:")` lines
</details>

<details>
<summary><b>❌ "ONNX model download failed"</b></summary>

The embedding model downloads on first run (~100MB). If it fails:
1. Check internet connectivity
2. Delete the ONNX cache: `rm -rf ~/.cache/huggingface/`
3. Restart the app — it will re-download
</details>

<details>
<summary><b>❌ LLM responds in wrong language</b></summary>

The LLM responds in the language detected by Whisper. If it detects the wrong language:
- Speak more clearly or for a longer duration
- The system prompt instructs the LLM to use the detected language
- Short messages may confuse language detection
</details>

### Health Check Endpoints

Use these to diagnose issues:

```bash
# Is the app running?
curl http://localhost:8080/actuator/health

# Are LLM and Vector Store working?
curl http://localhost:8080/api/diagnostics/health

# Test LLM directly
curl "http://localhost:8080/api/diagnostics/llm?prompt=Hello"

# Test Vector Store
curl "http://localhost:8080/api/diagnostics/vectorstore?query=rice+pest"
```

---

## 📊 API Rate Limits

| Service | Free Tier Limit | Our Usage |
|---------|----------------|-----------|
| Groq LLM | 6,000 TPM / 30 RPM | ~1,000 tokens/request ✅ |
| Groq Whisper | 28,800 req/day | ~1 req/voice message ✅ |
| HuggingFace TTS | ~300 req/hour | ~1 req/voice message ✅ |

---

## 🗺️ Roadmap

- [ ] 🔊 Fix TTS audio response (currently text-only)
- [ ] 🌍 Add location-aware advice using GPS coordinates
- [ ] 🖼️ Image-based crop disease detection via photo messages
- [ ] 📊 Farmer usage analytics dashboard
- [ ] 🗄️ PostgreSQL + pgvector for production RAG
- [ ] ☁️ Cloud deployment (Railway / Render / AWS)
- [ ] 📰 Real-time weather and market price integration
- [ ] 🌾 Expand KCC dataset (10,000+ Q&A pairs)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/my-feature`
3. Commit changes: `git commit -m 'feat(scope): add my feature'`
4. Push to branch: `git push origin feat/my-feature`
5. Open a Pull Request

---

## 📜 License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.

---

<p align="center">
  <b>Built with ❤️ for Indian farmers</b><br/>
  <sub>Powered by Spring AI • Groq • Telegram</sub>
</p>
