USE loja;

INSERT IGNORE INTO endereco (rua, bairro, numero, cep, usuario_id)
SELECT rua, bairro, numero, cep, id
FROM usuario;

ALTER TABLE usuario
    DROP COLUMN rua,
    DROP COLUMN bairro,
    DROP COLUMN numero,
    DROP COLUMN cep;
