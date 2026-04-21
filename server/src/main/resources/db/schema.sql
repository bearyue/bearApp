CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Asset table
CREATE TABLE IF NOT EXISTS asset (
    asset_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(user_id),
    category VARCHAR(20) NOT NULL,
    sub_type VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    currency VARCHAR(10) DEFAULT 'CNY',
    amount DECIMAL(18,4) DEFAULT 0,
    amount_cny DECIMAL(18,4) DEFAULT 0,
    cost DECIMAL(18,4),
    quantity DECIMAL(18,4),
    code VARCHAR(20),
    extra_json JSONB,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_asset_user_id ON asset(user_id);
CREATE INDEX IF NOT EXISTS idx_asset_category ON asset(user_id, category);

-- Net worth snapshot table
CREATE TABLE IF NOT EXISTS net_worth_snapshot (
    snapshot_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(user_id),
    snapshot_date DATE NOT NULL,
    total_asset DECIMAL(18,4),
    total_liability DECIMAL(18,4),
    net_worth DECIMAL(18,4),
    breakdown_json JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, snapshot_date)
);

-- Asset change log
CREATE TABLE IF NOT EXISTS asset_change_log (
    log_id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT REFERENCES asset(asset_id),
    user_id BIGINT NOT NULL REFERENCES sys_user(user_id),
    change_type VARCHAR(20) NOT NULL,
    old_value JSONB,
    new_value JSONB,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Exchange rate table
CREATE TABLE IF NOT EXISTS exchange_rate (
    rate_id BIGSERIAL PRIMARY KEY,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) DEFAULT 'CNY',
    rate DECIMAL(12,6) NOT NULL,
    rate_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(from_currency, to_currency, rate_date)
);
