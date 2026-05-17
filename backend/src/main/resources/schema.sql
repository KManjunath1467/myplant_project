-- MyPlant Database Schema
-- ========================================
-- This SQL file creates the database structure for MyPlant application
-- 
-- Tables:
-- 1. users - User accounts and authentication
-- 2. plant_care_rules - Plant knowledge database
-- 3. plants - User's plants
-- 4. watering_history - Watering event tracking
-- 5. notifications - Notification records
-- ========================================

-- Create Database
CREATE DATABASE IF NOT EXISTS myplant;
USE myplant;

-- ========================================
-- 1. USERS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email_notifications BOOLEAN DEFAULT TRUE,
    whatsapp_notifications BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- 2. PLANT_CARE_RULES TABLE
-- Plant Knowledge Database
-- ========================================
CREATE TABLE IF NOT EXISTS plant_care_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plant_name VARCHAR(100) UNIQUE NOT NULL,
    base_watering_days INT NOT NULL,
    watering_frequency VARCHAR(100) NOT NULL,
    sunlight_needs VARCHAR(100) NOT NULL,
    humidity_preference VARCHAR(100) NOT NULL,
    temperature_range VARCHAR(50) NOT NULL,
    difficulty_level VARCHAR(50) NOT NULL,
    description LONGTEXT,
    common_issues LONGTEXT,
    care_tips LONGTEXT,
    summer_watering_multiplier INT DEFAULT 1,
    winter_watering_multiplier INT DEFAULT 1,
    rain_sensitive BOOLEAN DEFAULT TRUE,
    temperature_sensitive BOOLEAN DEFAULT TRUE,
    recommended_pot_size VARCHAR(100),
    growth_rate VARCHAR(50),
    max_size VARCHAR(100),
    INDEX idx_plant_name (plant_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- 3. PLANTS TABLE
-- User's Plants
-- ========================================
CREATE TABLE IF NOT EXISTS plants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plant_care_rule_id BIGINT,
    name VARCHAR(100) NOT NULL,
    plant_type VARCHAR(100) NOT NULL,
    pot_size VARCHAR(100) NOT NULL,
    is_indoor BOOLEAN NOT NULL,
    location VARCHAR(100) NOT NULL,
    last_watered_date DATE,
    watering_streak INT DEFAULT 0,
    health VARCHAR(50) DEFAULT 'Healthy',
    notes LONGTEXT,
    custom_watering_interval INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plant_care_rule_id) REFERENCES plant_care_rules(id),
    INDEX idx_user_id (user_id),
    INDEX idx_plant_type (plant_type),
    INDEX idx_last_watered (last_watered_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- 4. WATERING_HISTORY TABLE
-- Tracks When Plants Were Watered
-- ========================================
CREATE TABLE IF NOT EXISTS watering_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    watered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes LONGTEXT,
    on_time BOOLEAN DEFAULT TRUE,
    water_amount VARCHAR(100),
    plant_health_at_watering VARCHAR(100),
    weather_condition VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE,
    INDEX idx_plant_id (plant_id),
    INDEX idx_watered_date (watered_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- 5. NOTIFICATIONS TABLE
-- Notification Records
-- ========================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plant_id BIGINT,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message LONGTEXT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    viewed_at TIMESTAMP NULL,
    failure_reason LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- INSERT SAMPLE PLANT CARE RULES DATA
-- ========================================
INSERT INTO plant_care_rules (
    plant_name, base_watering_days, watering_frequency, sunlight_needs,
    humidity_preference, temperature_range, difficulty_level, description,
    common_issues, care_tips, recommended_pot_size, growth_rate, max_size
) VALUES
('Snake Plant', 10, 'Every 10 days', 'Low to Medium',
 'Low', '16-24°C', 'Beginner',
 'Hardy succulent with dark green leaves. Very low maintenance and tolerates neglect.',
 'Root rot (overwatering), Brown tips, Pests',
 'Let soil dry between waterings. Avoid direct hot sun. Rotate pot monthly.',
 'Medium (6 inches)', 'Slow', '12-15 inches'),

('Pothos', 7, 'Every 7 days', 'Medium to Bright Indirect',
 'Moderate', '18-25°C', 'Beginner',
 'Trailing plant with heart-shaped leaves. Perfect for hanging baskets or vines on walls.',
 'Yellowing leaves (overwatering), Leggy growth (low light), Brown leaf tips (low humidity)',
 'Water when top inch of soil is dry. Provide bright indirect light. Prune regularly for bushier growth.',
 'Small to Medium (4-6 inches)', 'Fast', '6-10 feet (when trailing)'),

('Monstera Deliciosa', 7, 'Every 7 days', 'Bright Indirect Light',
 'High', '20-25°C', 'Intermediate',
 'Large tropical plant with distinctive split leaves. Makes dramatic statement indoors.',
 'Brown leaf tips (low humidity), Root rot (overwatering), Leggy growth (low light)',
 'Water when top 1 inch of soil is dry. Provide humidity with misting or pebble tray. Support with moss pole.',
 'Large (8-10 inches)', 'Fast', '3-4 feet indoors'),

('ZZ Plant', 14, 'Every 14 days', 'Low to Bright Indirect',
 'Low to Moderate', '16-25°C', 'Beginner',
 'Glossy compound leaves on upright stems. Extremely drought tolerant and stylish.',
 'Root rot (overwatering), Yellowing stems (waterlogged)', 'Prefers to dry out between waterings. Tolerates low light well. Wipe leaves monthly.',
 'Medium (6 inches)', 'Slow', '18-24 inches'),

('Spider Plant', 7, 'Every 7 days', 'Bright to Medium',
 'Moderate', '16-24°C', 'Beginner',
 'Grass-like leaves with white stripes. Produces baby plantlets (spiderettes) easily.',
 'Brown tips (low humidity or chlorine in water), Pale leaves (low light), Root rot',
 'Use filtered water to avoid brown tips. Keep soil consistently moist during growing season. Divide plantlets to propagate.',
 'Small to Medium (4-6 inches)', 'Fast', '12-15 inches'),

('Peace Lily', 5, 'Every 5 days', 'Low to Medium',
 'High', '18-25°C', 'Beginner',
 'Elegant white flowers (spathes) appear in spring. Plant wilts when thirsty.',
 'Brown leaf tips (low humidity), Drooping leaves (underwatering), Brown spots (fungi)',
 'Keep soil moist but not waterlogged. High humidity is important. Flowers last several months.',
 'Medium (6 inches)', 'Medium', '24-36 inches'),

('Rubber Plant', 10, 'Every 10 days', 'Bright Indirect Light',
 'Moderate', '18-25°C', 'Intermediate',
 'Large plant with thick, waxy, deep green leaves. Statement plant for living room.',
 'Leaf drop (cold drafts), Yellow leaves (overwatering), Brown spots (pest infection)',
 'Let top inch of soil dry between watering. Provide bright indirect light. Wipe leaves monthly to keep shiny.',
 'Large (8-10 inches)', 'Medium', '3-6 feet indoors'),

('Cactus', 14, 'Every 14 days', 'Bright Light (6+ hours)',
 'Low', '10-25°C', 'Beginner',
 'Succulent with spines. Very drought tolerant and unique decorative plant.',
 'Root rot (overwatering), Wrinkled appearance (underwatering)', 'Water thoroughly but rarely - monthly in winter. Needs well-draining soil. Position in sunny spot.',
 'Small (3-4 inches)', 'Slow', 'Varies by species'),

('Aloe Vera', 21, 'Every 21 days', 'Bright Light',
 'Low', '15-25°C', 'Beginner',
 'Gel-filled leaves can be used for skin care. Very low maintenance succulent.',
 'Root rot (overwatering), Shriveled leaves (drought)', 'Water sparingly - every 3 weeks or when soil is completely dry. Must have well-draining soil. Keep in warm location.',
 'Medium (6 inches)', 'Slow', '12-18 inches'),

('Philodendron', 7, 'Every 7 days', 'Medium to Bright',
 'Moderate to High', '18-25°C', 'Beginner',
 'Heart-shaped leaves on climbing vines. Adaptable and versatile houseplant.',
 'Yellow leaves (overwatering), Brown patches (low humidity), Pests (spider mites)',
 'Let top 1 inch of soil dry before watering. Provide support for climbing varieties. Prune for shape control.',
 'Medium (6 inches)', 'Fast', '4-6 feet');

-- ========================================
-- CREATE INDEXES FOR PERFORMANCE
-- ========================================
-- Already included in table definitions

-- ========================================
-- SQL QUERIES FOR COMMON OPERATIONS
-- ========================================

-- Get plants that need watering today (example)
-- SELECT p.id, p.name, p.last_watered_date, 
--        DATEDIFF(CURDATE(), p.last_watered_date) as days_since_watering,
--        pcr.base_watering_days
-- FROM plants p
-- LEFT JOIN plant_care_rules pcr ON p.plant_care_rule_id = pcr.id
-- WHERE DATEDIFF(CURDATE(), p.last_watered_date) >= COALESCE(pcr.base_watering_days, 7)
-- ORDER BY p.last_watered_date ASC;

-- Get user's plants with care information
-- SELECT p.id, p.name, p.plant_type, p.location, p.is_indoor,
--        p.last_watered_date, pcr.base_watering_days, pcr.watering_frequency
-- FROM plants p
-- LEFT JOIN plant_care_rules pcr ON p.plant_care_rule_id = pcr.id
-- WHERE p.user_id = 1
-- ORDER BY p.created_at DESC;
