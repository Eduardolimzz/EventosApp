#  EventosApp

<div align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Thymeleaf-3.0+-green?style=for-the-badge&logo=thymeleaf" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/Railway-Deploy-purple?style=for-the-badge&logo=railway" alt="Railway">
</div>

<div align="center">
  <h3>Gestão Profissional de Eventos</h3>
  <p>Plataforma completa para organizar eventos e gerenciar convidados com eficiência, controle total e design moderno</p>
  
  <a href="https://javaspringboot-production-a11a.up.railway.app/">🌐 Clique e veja!</a>
</div>

---

## Sobre o Projeto

O **EventosApp** é um sistema web completo desenvolvido com **Java 17** e **Spring Boot** que permite o gerenciamento profissional de eventos. A aplicação oferece uma interface moderna e intuitiva para criar eventos, gerenciar convidados e acompanhar confirmações de presença em tempo real.

###  Principais Funcionalidades

-  **Gestão Completa de Eventos** - Criar, editar e excluir eventos
-  **Gerenciamento de Convidados** - Adicionar e controlar lista de convidados  
-  **Links Públicos** - Confirmação de presença via link compartilhável
-  **Dashboard Administrativo** - Acompanhar confirmações e estatísticas
-  **Sistema de Autenticação** - Login seguro com controle de sessão
-  **Validação de Dados** - Validação robusta em frontend e backend
-  **Design Responsivo** - Interface moderna que funciona em todos os dispositivos

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.5.3** - Framework principal
- **Spring MVC** - Arquitetura Model-View-Controller
- **Spring Data JPA** - Persistência de dados
- **Bean Validation** - Validação de dados
- **HikariCP** - Pool de conexões

### Frontend
- **Thymeleaf** - Template engine
- **HTML5/CSS3** - Estrutura e estilização
- **JavaScript** - Interatividade (AJAX)
- **Materialize CSS** - Framework de design

### Banco de Dados
- **MySQL 8.0+** - Banco principal
- **Railway MySQL** - Banco em produção

### Deploy & DevOps
- **Railway** - Plataforma de deploy
- **Maven** - Gerenciamento de dependências
- **Git/GitHub** - Controle de versão

---

## Como Executar o Projeto

### Pré-requisitos

Certifique-se de ter instalado:
- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/)
- [Git](https://git-scm.com/downloads)

### Instalação Local

1. **Clone o repositório**
```bash
git clone https://github.com/Eduardolimzz/Java_Spring_Boot.git
cd Java_Spring_Boot/eventosapp
```

2. **Configure o banco de dados**
```bash
# Entre no MySQL
mysql -u root -p

# Crie o banco de dados
CREATE DATABASE eventosdb;
EXIT;
```

3. **Configure as propriedades da aplicação**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventosdb
spring.datasource.username=root
spring.datasource.password=suasenha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
server.port=8080
```

4. **Execute a aplicação**
```bash
./mvnw spring-boot:run
```

5. **Acesse a aplicação**
```
http://localhost:8080
```

---

## Como Usar

### 1. **Página Inicial**
- Acesse a página principal com design moderno
- Faça login ou crie uma conta
- Navegue pelas funcionalidades disponíveis

### 2. **Gerenciar Eventos**
- Clique em "CADASTRAR EVENTO" para criar um novo evento
- Preencha: Nome, Local, Data, Horário e Descrição
- Visualize todos os seus eventos na "Lista de Eventos"

### 3. **Gerenciar Convidados**
- Acesse os detalhes de qualquer evento
- Adicione convidados informando Nome e E-mail
- Acompanhe o status das confirmações

### 4. **Compartilhar Evento**
- Copie o link público gerado automaticamente
- Envie para os convidados confirmarem presença
- Acompanhe as confirmações em tempo real

---

## API Endpoints

### Públicos (Não requer autenticação)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/confirmar/{codigo}` | Página de confirmação de presença |
| `POST` | `/confirmar/{codigo}` | Submeter confirmação de presença |

### Privados (Requer autenticação)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/eventos` | Lista todos os eventos do usuário |
| `GET` | `/cadastrarEvento` | Formulário para criar evento |
| `POST` | `/cadastrarEvento` | Cadastrar novo evento |
| `GET` | `/detalhesEvento/{codigo}` | Detalhes do evento e convidados |
| `POST` | `/detalhesEvento/{codigo}` | Adicionar convidado ao evento |
| `GET` | `/deletarEvento/{codigo}` | Excluir evento |
| `POST` | `/alterarStatusConvidado` | Alterar status via AJAX |
| `POST` | `/editarConvidado` | Editar dados do convidado |

---

## Estrutura do Projeto

```
eventosapp/
├── src/main/java/com/eventosapp/
│   ├── controllers/          # Controladores MVC
│   │   ├── AuthController.java
│   │   ├── EventoController.java
│   │   ├── IndexController.java
│   │   └── PublicController.java
│   ├── models/              # Entidades JPA
│   │   ├── Convidado.java
│   │   ├── Evento.java
│   │   └── Usuario.java
│   ├── repository/          # Repositórios de dados
│   │   ├── ConvidadoRepository.java
│   │   ├── EventoRepository.java
│   │   └── UsuarioRepository.java
│   └── service/            # Lógica de negócio
│       └── AuthService.java
├── src/main/resources/
│   ├── templates/          # Templates Thymeleaf
│   │   ├── evento/
│   │   └── public/
│   ├── static/            # Arquivos estáticos
│   └── application.properties
└── pom.xml               # Dependências Maven
```

---

## Configuração Avançada

### Variáveis de Ambiente (Produção)
```bash
DATABASE_URL=mysql://user:password@host:port/database
PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

---

## Deploy

### Railway (Recomendado)
1. Conecte seu repositório GitHub ao Railway
2. Configure as variáveis de ambiente
3. O deploy é automático a cada push

---

## Contribuindo

Contribuições são sempre bem-vindas! Para contribuir:

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## Reportar Problemas

Encontrou um bug? [Abra uma issue](https://github.com/Eduardolimzz/Java_Spring_Boot/issues) descrevendo:
- Passos para reproduzir
- Comportamento esperado vs atual
- Screenshots (se aplicável)
- Informações do ambiente

---

## Roadmap

- [ ] Sistema de notificações por email
- [ ] Dashboard com gráficos avançados
- [ ] API REST completa
- [ ] Integração com calendários
- [ ] Sistema de templates de eventos

---

## Autor

<div align="center">
  <img src="https://github.com/Eduardolimzz.png" width="100px" style="border-radius: 50%">
  
  **Eduardo Lima dos Santos**
  
  [![GitHub](https://img.shields.io/badge/-GitHub-181717?style=flat&logo=github)](https://github.com/Eduardolimzz)
  [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin)](https://linkedin.com/in/eduardolimzz)
  
  *Desenvolvedor Full Stack*
</div>

---

<div align="center">
  <p>Se este projeto te ajudou, considere dar uma estrela!</p>
</div>
