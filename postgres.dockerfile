FROM postgres:12-alpine

COPY criar_tabelas.sql /docker-entrypoint-initdb.d/
   
   