package com.kisanconnect.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;

/**
 * Seeds the vector store with sample KCC agriculture data on startup (dev
 * only).
 * This provides immediate RAG context for testing advisory generation.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SampleDataSeeder {

    /**
     * Load sample KCC (Kisan Call Centre) Q&A data into the vector store at
     * startup.
     * Only runs in dev profile.
     */
    @Bean
    @Profile("dev")
    CommandLineRunner seedSampleData(VectorStore vectorStore) {
        return args -> {
            log.info("🌾 Seeding sample KCC agriculture data into vector store...");

            List<Document> documents = createSampleKccData();
            vectorStore.add(documents);

            log.info("✅ Seeded {} sample KCC documents into vector store", documents.size());
        };
    }

    private List<Document> createSampleKccData() {
        return List.of(
                // Pest Management
                kccDoc("How to control brown plant hopper in rice?",
                        "For brown plant hopper (BPH) control in paddy: 1) Drain excess water from the field. " +
                                "2) Avoid close planting. 3) Spray Imidacloprid 17.8% SL @ 3ml per 10 liters of water or "
                                +
                                "Buprofezin 25% SC @ 20ml per 10 liters. 4) Spray at the base of the plant, not on leaves. "
                                +
                                "5) Use light traps to monitor BPH population. 6) Avoid excessive nitrogen fertilizer.",
                        "Rice", "Pest Management", "West Bengal"),

                kccDoc("What is the treatment for fall armyworm in maize?",
                        "For Fall Armyworm in maize: 1) Apply Emamectin benzoate 5% SG @ 4g per 10 liters of water. " +
                                "2) Or use Spinetoram 11.7% SC @ 4.5ml per 10 liters. 3) Spray into the whorl of the plant. "
                                +
                                "4) Use pheromone traps (5 per acre) for monitoring. 5) Release Trichogramma egg parasitoids "
                                +
                                "@ 1 lakh per acre as biological control. 6) Apply neem oil 5% as a preventive measure.",
                        "Maize", "Pest Management", "Karnataka"),

                kccDoc("How to manage cotton bollworm?",
                        "For cotton bollworm management: 1) Install pheromone traps @ 5/acre for monitoring. " +
                                "2) Spray Neem Seed Kernel Extract (NSKE) 5% at early stage. " +
                                "3) Release Trichogramma chilonis @ 1.5 lakh per acre. " +
                                "4) At ETL (Economic Threshold Level), spray Emamectin benzoate 5% SG @ 4g/10L water. "
                                +
                                "5) Rotate insecticides to prevent resistance. 6) Pick and destroy damaged bolls.",
                        "Cotton", "Pest Management", "Maharashtra"),

                // Soil & Fertilizer
                kccDoc("What is the recommended fertilizer dose for wheat?",
                        "Recommended fertilizer dose for wheat (per acre): 1) Apply 50kg Urea + 50kg DAP + 20kg MOP " +
                                "as basal dose at sowing. 2) Top dress with 25kg Urea at first irrigation (21 days). " +
                                "3) Second top dressing of 25kg Urea at second irrigation (45 days). " +
                                "4) Apply 10kg Zinc Sulphate if soil is zinc deficient. " +
                                "5) For irrigated conditions, total N:P:K = 120:60:40 kg/ha.",
                        "Wheat", "Soil & Fertilizer", "Punjab"),

                kccDoc("How to improve soil health in paddy fields?",
                        "To improve soil health in paddy: 1) Apply FYM (Farm Yard Manure) @ 10-12 tonnes per hectare " +
                                "before transplanting. 2) Practice green manuring with dhaincha or sunhemp. " +
                                "3) Apply bio-fertilizers — Azospirillum @ 2kg/acre and PSB @ 2kg/acre. " +
                                "4) Incorporate rice straw back into the field instead of burning. " +
                                "5) Maintain proper field drainage. 6) Test soil every 2-3 years.",
                        "Rice", "Soil & Fertilizer", "Tamil Nadu"),

                // Disease Management
                kccDoc("How to control yellow rust in wheat?",
                        "For yellow rust in wheat: 1) Spray Propiconazole 25% EC @ 10ml per 10 liters of water. " +
                                "2) Or use Tebuconazole 25.9% EC @ 10ml per 10 liters. " +
                                "3) First spray at symptom appearance, repeat after 15 days if needed. " +
                                "4) Use resistant varieties like HD-3086, WH-1105, PBW-725. " +
                                "5) Avoid late sowing. Optimal sowing: Nov 1-20.",
                        "Wheat", "Disease Management", "Haryana"),

                kccDoc("What causes leaf curl in chilli and how to treat it?",
                        "Chilli leaf curl is caused by whitefly-transmitted begomovirus. Treatment: " +
                                "1) Control whitefly vector with Imidacloprid 17.8% SL @ 3ml/10L water. " +
                                "2) Apply neem oil 2% at 10ml/L water as repellent. " +
                                "3) Use yellow sticky traps @ 12 per acre. " +
                                "4) Remove and destroy infected plants. " +
                                "5) Use virus-free seedlings and resistant varieties. " +
                                "6) Spray Thiamethoxam 25% WG @ 3g/10L as alternative.",
                        "Chilli", "Disease Management", "Andhra Pradesh"),

                // Water Management
                kccDoc("How much water does sugarcane need and when to irrigate?",
                        "Sugarcane water management: 1) First irrigation immediately after planting. " +
                                "2) Germination phase (0-45 days): irrigate every 7-10 days. " +
                                "3) Tillering phase (45-120 days): irrigate every 10-12 days. " +
                                "4) Grand growth phase (120-270 days): irrigate every 7-8 days — critical period. " +
                                "5) Maturity phase (270-360 days): reduce irrigation, withhold 15 days before harvest. "
                                +
                                "6) Total water requirement: 2000-2500mm per crop. " +
                                "7) Use drip irrigation to save 30-40% water.",
                        "Sugarcane", "Water Management", "Uttar Pradesh"),

                // Crop Advisory
                kccDoc("When should I sow mustard and what variety is best?",
                        "Mustard sowing advisory: 1) Best sowing time: October 15 to November 15. " +
                                "2) Recommended varieties: Pusa Mustard-25, RH-749, NRCHB-101. " +
                                "3) Seed rate: 1.5-2 kg per acre. " +
                                "4) Row spacing: 30cm, plant to plant: 10-15cm. " +
                                "5) Seed treatment: Thiram @ 2.5g/kg seed. " +
                                "6) Apply 32kg Urea + 50kg SSP + 10kg MOP per acre at sowing. " +
                                "7) First irrigation at 25-30 days (pre-flowering stage is critical).",
                        "Mustard", "Crop Advisory", "Rajasthan"),

                kccDoc("How to increase yield of paddy rice?",
                        "To increase paddy yield: 1) Use certified high-yielding varieties (Swarna, IR-64, Pusa-44). " +
                                "2) Practice SRI (System of Rice Intensification) — single seedling, wider spacing 25x25cm. "
                                +
                                "3) Apply balanced NPK fertilizer 120:60:40 kg/ha. " +
                                "4) Use micronutrients — Zinc sulphate 25kg/ha. " +
                                "5) Proper water management — alternate wetting and drying saves water, increases yield. "
                                +
                                "6) Weed management — apply Butachlor before weeding. " +
                                "7) Harvest at 80% grain maturity for best quality.",
                        "Rice", "Crop Advisory", "Odisha"),

                // Organic Farming
                kccDoc("How to prepare vermicompost at home?",
                        "Vermicompost preparation: 1) Build a bed 6ft x 3ft x 2ft with bricks or cement rings. " +
                                "2) Layer bottom with dry leaves, then cow dung, then kitchen waste. " +
                                "3) Add 1kg earthworms (Eisenia fetida) per bed. " +
                                "4) Maintain moisture at 60-70% — sprinkle water daily. " +
                                "5) Cover with gunny bags to prevent sunlight. " +
                                "6) Turn the compost every 15 days. " +
                                "7) Ready in 45-60 days — dark brown, earthy smell. " +
                                "8) Yield: approximately 300-400 kg per cycle.",
                        "General", "Organic Farming", "All India"),

                kccDoc("What are the benefits of neem-based pesticides?",
                        "Neem-based pesticide benefits: 1) Azadirachtin disrupts insect growth and reproduction. " +
                                "2) Effective against 200+ pest species including aphids, whiteflies, and caterpillars. "
                                +
                                "3) Preparation: Crush 5kg neem seeds, soak in 10L water overnight, filter and spray. "
                                +
                                "4) Or use commercial Neem Oil 0.03% EC @ 5ml/L water. " +
                                "5) Safe for beneficial insects like bees and ladybugs. " +
                                "6) No pesticide residue on produce. " +
                                "7) Improves soil health when neem cake is applied @ 250kg/hectare.",
                        "General", "Organic Farming", "All India"));
    }

    /**
     * Helper to create a KCC-style document with metadata.
     */
    private Document kccDoc(String question, String answer, String crop,
            String category, String state) {
        String content = String.format("Question: %s\nAnswer: %s", question, answer);
        Map<String, Object> metadata = Map.of(
                "source", "kcc",
                "crop", crop,
                "category", category,
                "state", state);
        return new Document(content, metadata);
    }
}
