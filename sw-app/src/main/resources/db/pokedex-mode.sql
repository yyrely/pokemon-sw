ALTER TABLE sw_user
    ADD COLUMN collect_mode VARCHAR(32) NULL DEFAULT 'scale_world' COMMENT 'collect mode: pokedex or scale_world',
    ADD COLUMN onboarding_version INT NOT NULL DEFAULT 0 COMMENT 'completed onboarding version',
    ADD COLUMN pokemon_collect_sync_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 not synced, 1 synced',
    ADD COLUMN pokemon_collect_sync_time DATETIME NULL COMMENT 'last successful pokedex sync time',
    ADD COLUMN pokemon_collect_sync_error VARCHAR(512) NULL COMMENT 'last pokedex sync failure reason';

UPDATE sw_user
SET collect_mode = 'scale_world'
WHERE collect_mode IS NULL;

ALTER TABLE sw_user
    MODIFY COLUMN collect_mode VARCHAR(32) NOT NULL DEFAULT 'scale_world' COMMENT 'collect mode: pokedex or scale_world';

CREATE TABLE IF NOT EXISTS sw_pokemon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_user BIGINT NULL,
    create_time DATETIME NULL,
    update_user BIGINT NULL,
    update_time DATETIME NULL,
    national_no INT NOT NULL COMMENT 'national pokedex number',
    region_id BIGINT NOT NULL COMMENT 'region id',
    generation_no INT NOT NULL COMMENT 'generation number',
    name_zh VARCHAR(80) NOT NULL COMMENT 'Chinese display name',
    name_en VARCHAR(80) NOT NULL COMMENT 'English display name',
    api_species_name VARCHAR(80) NOT NULL COMMENT 'PokeAPI species name',
    api_pokemon_name VARCHAR(80) NOT NULL COMMENT 'PokeAPI pokemon name',
    type_names VARCHAR(255) NULL COMMENT 'type names json',
    sprite_url VARCHAR(512) NULL COMMENT 'source image url',
    local_img VARCHAR(512) NULL COMMENT 'local image path',
    sort INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sw_pokemon_national_no (national_no),
    KEY idx_sw_pokemon_region_sort (region_id, sort)
);

CREATE TABLE IF NOT EXISTS sw_pokemon_alias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_user BIGINT NULL,
    create_time DATETIME NULL,
    update_user BIGINT NULL,
    update_time DATETIME NULL,
    pokemon_id BIGINT NOT NULL,
    alias VARCHAR(80) NOT NULL,
    alias_type VARCHAR(32) NOT NULL DEFAULT 'manual',
    UNIQUE KEY uk_sw_pokemon_alias (pokemon_id, alias),
    KEY idx_sw_pokemon_alias_name (alias)
);

CREATE TABLE IF NOT EXISTS sw_collectible_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_user BIGINT NULL,
    create_time DATETIME NULL,
    update_user BIGINT NULL,
    update_time DATETIME NULL,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(40) NOT NULL,
    sort INT NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    UNIQUE KEY uk_sw_collectible_type_code (code)
);

INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'figure', '手办', 10, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'figure');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'scale_world', '比例世界', 11, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'scale_world');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'card', '卡牌', 20, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'card');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'magnet', '冰箱贴', 30, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'magnet');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'badge', '徽章', 40, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'badge');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'capsule_toy', '扭蛋', 50, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'capsule_toy');
INSERT INTO sw_collectible_type (code, name, sort, enabled)
SELECT 'other', '其他', 999, 1
WHERE NOT EXISTS (SELECT 1 FROM sw_collectible_type WHERE code = 'other');

CREATE TABLE IF NOT EXISTS sw_user_pokemon_collect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_user BIGINT NULL,
    create_time DATETIME NULL,
    update_user BIGINT NULL,
    update_time DATETIME NULL,
    user_id BIGINT NOT NULL,
    pokemon_id BIGINT NOT NULL,
    collectible_type_code VARCHAR(32) NOT NULL,
    collect_date DATE NULL,
    collect_price DECIMAL(10,2) NULL,
    remark VARCHAR(255) NULL,
    exchange_status TINYINT(1) NOT NULL DEFAULT 0,
    exchange_remark VARCHAR(255) NULL,
    source_type VARCHAR(32) NULL COMMENT 'scale_item or scale_sub_item',
    source_collect_id BIGINT NULL COMMENT 'source collect record id',
    source_id BIGINT NULL COMMENT 'item_id or sub_item_id',
    KEY idx_sw_user_pokemon_collect_user_pokemon (user_id, pokemon_id),
    KEY idx_sw_user_pokemon_collect_user_type (user_id, collectible_type_code),
    KEY idx_sw_user_pokemon_collect_exchange (exchange_status),
    UNIQUE KEY uk_user_pokemon_scale_source (user_id, pokemon_id, source_type, source_collect_id, source_id)
);

CREATE TABLE IF NOT EXISTS sw_pokemon_scale_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_user BIGINT NULL,
    create_time DATETIME NULL,
    update_user BIGINT NULL,
    update_time DATETIME NULL,
    pokemon_id BIGINT NOT NULL,
    source_type VARCHAR(32) NOT NULL COMMENT 'item or sub_item',
    source_id BIGINT NOT NULL,
    match_type VARCHAR(32) NOT NULL COMMENT 'auto_name, manual, ignored',
    match_score DECIMAL(5,2) NULL,
    confirmed TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sw_pokemon_scale_mapping_unique (pokemon_id, source_type, source_id),
    KEY idx_sw_pokemon_scale_mapping_source (source_type, source_id),
    KEY idx_sw_pokemon_scale_mapping_pokemon (pokemon_id)
);


CREATE TABLE IF NOT EXISTS sw_pokemon_type (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     deleted TINYINT(1) NOT NULL DEFAULT 0,
     create_user BIGINT NULL,
     create_time DATETIME NULL,
     update_user BIGINT NULL,
     update_time DATETIME NULL,
     code VARCHAR(50),
     name_zh VARCHAR(50),
     name_en VARCHAR(50),
     icon_url VARCHAR(255),
     local_icon VARCHAR(255),
     sort INT
);
