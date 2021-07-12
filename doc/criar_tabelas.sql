CREATE DATABASE grupo6;

\c grupo6


CREATE TABLE itens (
    "item_id" SERIAL, --( INCREMENT 1 START 1 ),
    "nome" character varying(50) NOT NULL,
    "descricao" character varying (300),
    
    PRIMARY KEY ("item_id"),
    CONSTRAINT "unique_nome" UNIQUE ("nome")
);

create table entregas (
	"entrega_id" SERIAL,
	"morada" character varying(100) NOT NULL,
	"data_entrega" date not null,
	
	PRIMARY KEY ("entrega_id"),
	CONSTRAINT "unique_morada_data" UNIQUE ("morada", "data_entrega")
);


create table depositos (
	"deposito_id" SERIAL,
	"quantidade" integer NOT NULL,
	"id_item" integer NOT NULL,

	PRIMARY KEY ("deposito_id"),

	CONSTRAINT "fk_item_deposito" FOREIGN KEY ("id_item") REFERENCES itens ("item_id")
);

create table listaItens (
	"referencia_id" SERIAL,
	"ref_entrega_id" integer NOT NULL,
	"ref_item_id" integer NOT NULL,
	"quantidade_item" integer NOT NULL, 

	PRIMARY KEY ("referencia_id"),

	CONSTRAINT "unique_entrega_Nome" UNIQUE ("ref_entrega_id", "ref_item_id"),
	CONSTRAINT "fk_ref_entrega" FOREIGN KEY ("ref_entrega_id") REFERENCES entregas ("entrega_id"),
	CONSTRAINT "fk_ref_item" FOREIGN KEY ("ref_item_id") REFERENCES itens ("item_id")	
);


-- APAGAR Tabelas

--DROP TABLE listaItens;
--DROP TABLE depositos;
--DROP TABLE entregas;
--DROP TABLE itens;