# Project_Store - Backend

## Build e iniciar o servidor (bash)

```bash
javac -cp "target/dep/*" -d target/classes $(find src/main/java -name "*.java") && java -cp "target/classes;target/dep/*" com.loja.crud.Main
```

A API sobe em http://localhost:8080/api
