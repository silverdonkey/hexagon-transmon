-- Create Table: public.account
CREATE TABLE IF NOT EXISTS public.account
(
    id bigint NOT NULL,
    CONSTRAINT account_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.account
    OWNER to postgres;

-- Create Table: public.activity
CREATE TABLE IF NOT EXISTS public.activity
(
    id bigint NOT NULL,
    "timestamp" timestamp(6) without time zone,
    owner_account_id bigint,
    source_account_id bigint,
    target_account_id bigint,
    amount bigint,
    CONSTRAINT activity_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.activity
    OWNER to postgres;

-- Insert Data
insert into account (id) values (1);
insert into account (id) values (2);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (1, '2018-08-08 08:00:00.0', 1, 1, 2, 500);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (2, '2018-08-08 08:00:00.0', 2, 1, 2, 500);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (3, '2018-08-09 10:00:00.0', 1, 2, 1, 1000);