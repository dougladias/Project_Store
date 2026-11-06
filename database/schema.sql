CREATE DATABASE IF NOT EXISTS loja
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE loja;

CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    data_nascimento DATE NOT NULL,
    CONSTRAINT uk_usuario_cpf UNIQUE (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS endereco (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    rua VARCHAR(150) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    cep CHAR(8) NOT NULL,
    CONSTRAINT uk_endereco_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_endereco_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    unidade VARCHAR(5) NOT NULL DEFAULT 'UN',
    preco DECIMAL(10, 2) NOT NULL,
    quantidade DECIMAL(10, 3) NOT NULL,
    imagem MEDIUMTEXT NULL,
    CONSTRAINT chk_produto_preco CHECK (preco > 0),
    CONSTRAINT chk_produto_quantidade CHECK (quantidade >= 0),
    CONSTRAINT chk_produto_unidade CHECK (unidade IN ('UN', 'KG', 'G', 'L', 'ML'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS compra (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    forma_pagamento VARCHAR(20) NULL,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_finalizacao DATETIME NULL,
    CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT chk_compra_valor CHECK (valor_total >= 0),
    CONSTRAINT chk_compra_status CHECK (status IN ('ABERTA', 'FINALIZADA')),
    CONSTRAINT chk_compra_pagamento CHECK (
        forma_pagamento IS NULL
        OR forma_pagamento IN ('PIX', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'BOLETO', 'DINHEIRO')
    ),
    CONSTRAINT chk_compra_finalizada CHECK (
        status = 'ABERTA'
        OR (forma_pagamento IS NOT NULL AND data_finalizacao IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS compra_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uk_compra_item_produto UNIQUE (compra_id, produto_id),
    CONSTRAINT fk_compra_item_compra FOREIGN KEY (compra_id) REFERENCES compra (id) ON DELETE CASCADE,
    CONSTRAINT fk_compra_item_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
    CONSTRAINT chk_compra_item_quantidade CHECK (quantidade > 0),
    CONSTRAINT chk_compra_item_preco CHECK (preco_unitario > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
