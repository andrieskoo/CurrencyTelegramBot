CREATE TABLE IF NOT EXISTS currency (
	iso4217code int4 NOT NULL,
	name varchar(255) NULL,
	CONSTRAINT currency_pkey PRIMARY KEY (iso4217code)
);

CREATE TABLE IF NOT EXISTS customer (
	is_bot bool NULL,
	is_premium bool NULL,
	created_at timestamp NULL,
	id bigserial NOT NULL,
	telegram_id int8 NULL,
	language_code varchar(255) NULL,
	lastname varchar(255) NULL,
	name varchar(255) NULL,
	CONSTRAINT customer_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS customer_settings (
	base_currency_iso4217code int4 NULL,
	notification_hour int4 NULL,
	customer_id int8 NOT NULL,
	bank varchar(255) NULL,
	CONSTRAINT customer_settings_bank_check CHECK (((bank)::text = ANY ((ARRAY['PRIVATBANK'::character varying, 'MONOBANK'::character varying, 'NBU'::character varying, 'NBP'::character varying])::text[]))),
	CONSTRAINT customer_settings_pkey PRIMARY KEY (customer_id)
);


-- public.customer_settings foreign keys

ALTER TABLE public.customer_settings ADD CONSTRAINT fkb8sqswhm630f9nn985wha821p FOREIGN KEY (base_currency_iso4217code) REFERENCES public.currency(iso4217code);
ALTER TABLE public.customer_settings ADD CONSTRAINT fko1eeasy40gl6srgw0f2oj1f7p FOREIGN KEY (customer_id) REFERENCES public.customer(id);



CREATE TABLE IF NOT EXISTS customer_settings_currency (
	iso4217code int4 NOT NULL,
	customer_id int8 NOT NULL
);


-- public.customer_settings_currency foreign keys

ALTER TABLE public.customer_settings_currency ADD CONSTRAINT fkeqc5uc7hv81tid9rcriib97hd FOREIGN KEY (iso4217code) REFERENCES public.currency(iso4217code);
ALTER TABLE public.customer_settings_currency ADD CONSTRAINT fklcj0ba06yeueynsmndrahbckw FOREIGN KEY (customer_id) REFERENCES public.customer_settings(customer_id);