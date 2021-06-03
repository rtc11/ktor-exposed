ALTER TABLE intermediate_zone_alternative ALTER COLUMN id DROP IDENTITY IF EXISTS;

CREATE SEQUENCE IF NOT EXISTS intermediate_zone_alternative_id_seq START 50000;

ALTER TABLE intermediate_zone_alternative ALTER COLUMN id SET DEFAULT nextval('intermediate_zone_alternative_id_seq');
ALTER TABLE intermediate_zone_alternative ALTER COLUMN id SET NOT NULL;

ALTER SEQUENCE intermediate_zone_alternative_id_seq OWNED BY intermediate_zone_alternative.id;
