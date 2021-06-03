create table authority(
    name varchar(255) not null constraint authority_pkey primary key
);
create table authority_id(
    id             varchar(255) not null constraint authority_id_pkey primary key,
    authority_name varchar(255) not null constraint fk_authority_id_authority references authority
);
create table line(
    id                 varchar(255) not null constraint line_pkey primary key,
    name               varchar(255) not null,
    transport_sub_mode varchar(255) not null,
    authority_id_id    varchar(255) not null constraint fk_line_authority_id references authority_id
);
create table municipality(
    id   varchar(255) not null constraint municipality_pkey primary key,
    name varchar(255) not null
);
create table stop_place(
    id              varchar(255) not null constraint stop_place_pkey primary key,
    description     varchar(255),
    name            varchar(255) not null,
    position        varchar(255) not null,
    municipality_id varchar(255) not null constraint fk_stop_place_municipality references municipality
);
create table stops_lines(
    line_id varchar(255) not null constraint fk_stops_lines_line references line,
    stop_id varchar(255) not null constraint fk_stops_lines_stop_place references stop_place
);
create table zone(
    id             varchar(255) not null constraint zone_pkey primary key,
    name           varchar(255) not null,
    authority_name varchar(255) not null constraint fk_zone_authority references authority
);
create table stops_zones(
    zone_id varchar(255) not null constraint fk_stops_zones_zone references zone,
    stop_id varchar(255) not null constraint fk_stops_zones_stop_place references stop_place
);
create table zone_connection(
    id           varchar(255) not null constraint zone_connection_pkey primary key,
    from_zone_id varchar(255) not null constraint fk_from_zone_connection_zone references zone,
    to_zone_id   varchar(255) not null constraint fk_to_zone_connection_zone references zone
);
create table intermediate_zone_alternative(
    id                 bigserial    not null constraint intermediate_zone_alternative_pkey primary key,
    distance           integer      not null,
    zone_connection_id varchar(255) not null constraint fk_intermediate_zone_zone_connection references zone_connection
);
create table zone_intermediate_zones(
    zone_id                          varchar(255) not null constraint fk_intermediate_join_zone references zone,
    intermediate_zone_alternative_id bigint       not null,
    zones_order                      integer default 0
);
create unique index intermediate_zone_alternative_id on intermediate_zone_alternative (id);
