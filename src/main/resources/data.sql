-- Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_STUDENT') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_PROVIDER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (3, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;

-- Subscription Plans
INSERT INTO subscription_plans (id, name, type, price, duration_days, max_applications, max_bookings, description, active)
VALUES
  (1, 'Free',     'FREE',    0.00,  365, 3,  2,  'Basic free access', true),
  (2, 'Basic',    'BASIC',   9.99,  30,  10, 5,  'Perfect for students starting their journey', true),
  (3, 'Premium',  'PREMIUM', 29.99, 30,  50, 20, 'Full access to all features', true),
  (4, 'Pro',      'PRO',     99.99, 30,  -1, -1, 'Unlimited access for power users', true)
ON CONFLICT (id) DO NOTHING;

-- Service Categories
INSERT INTO service_categories (id, name, description, icon, active)
VALUES
  (1, 'Visa Assistance',      'Help with student visa applications',      'visa',     true),
  (2, 'Accommodation',        'Finding student housing and hostels',      'home',     true),
  (3, 'Airport Pickup',       'Transport from airport to accommodation',  'flight',   true),
  (4, 'Bank Account Setup',   'Opening a student bank account',           'bank',     true),
  (5, 'Health Insurance',     'Student health and travel insurance',      'health',   true),
  (6, 'Language Courses',     'Language preparation and tutoring',        'language', true),
  (7, 'Career Counselling',   'Guidance for job placement abroad',        'career',   true),
  (8, 'Document Translation', 'Certified document translation',           'document', true)
ON CONFLICT (id) DO NOTHING;

-- Admin user (password: Admin@1234)
INSERT INTO users (
  id, email, password, first_name, last_name, phone,
  email_verified, active, created_at, updated_at
)
VALUES (
  1,
  'admin@overseasportal.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lM6.',
  'Portal',
  'Admin',
  '+1000000000',
  true,
  true,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

-- Admin role mapping
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 3)
ON CONFLICT DO NOTHING;

-- Reset PostgreSQL sequences after manual ID inserts
SELECT setval('roles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM roles), true);
SELECT setval('subscription_plans_id_seq', (SELECT COALESCE(MAX(id), 1) FROM subscription_plans), true);
SELECT setval('service_categories_id_seq', (SELECT COALESCE(MAX(id), 1) FROM service_categories), true);
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users), true);