# RDO Digital - Sistema de Gestão de Relatórios Diários de Obra

Sistema completo para gerenciamento de relatórios diários de obras (RDO), desenvolvido com arquitetura multi-tenant e integração com serviços cloud.

Link em produção: https://www.rdodigital.com.br - Teste agora mesmo!
## Visão Geral

O RDO Digital é uma sistema que permite construtoras e empresas do setor de construção civil gerenciar e documentar o andamento de suas obras de forma digital, substituindo os tradicionais relatórios em papel por um sistema moderno e eficiente.

### Principais Funcionalidades

- **Gestão Multi-tenant**: Isolamento completo de dados entre empresas
- **Relatórios Diários Detalhados**: Registro completo das atividades diárias de obras
- **Upload de Mídias**: Fotos e vídeos com suporte a múltiplos formatos (JPEG, PNG, HEIF, MP4, MOV, etc)
- **Geração de PDF**: Exportação de relatórios em PDF usando Playwright e Thymeleaf
- **Assinaturas Digitais**: Validação e registro de assinaturas em relatórios
- **Controle de Acesso**: Sistema robusto de permissões por usuário e obra
- **Sistema de Planos**: Integração com Stripe para cobranças recorrentes
- **Notificações por Email**: Templates HTML customizados para comunicações

## Stack Tecnológica

### Backend
- **Java 21** com Spring Boot 3.5.7
- **Spring Data JPA** + Hibernate
- **Spring Security** com JWT (Auth0)
- **PostgreSQL 17** como banco de dados
- **Hibernate Envers** para auditoria de entidades

### Infraestrutura e Serviços
- **AWS S3** para armazenamento de arquivos
- **Stripe** para gestão de pagamentos e assinaturas
- **Microsoft Playwright** para geração de PDFs
- **FFmpeg** para processamento de vídeos
- **Docker** + Docker Compose para containerização do banco de dados local

## Arquitetura

### Estrutura do Projeto

```
src/main/java/com/branches/
├── arquivo/           # Gestão de arquivos (fotos e vídeos)
├── assinaturadeplano/ # Sistema de assinaturas e cobranças
├── atividade/         # Atividades dentro dos relatórios
├── auth/              # Autenticação e autorização
├── comentarios/       # Comentários em relatórios
├── condicaoclimatica/ # Condições climáticas dos relatórios
├── config/            # Configurações (Security, CORS, Envers, etc)
├── configuradores/    # Modelos de relatórios customizáveis
├── equipamento/       # Controle de equipamentos
├── exception/         # Tratamento de exceções
├── external/          # Integrações externas (AWS, Stripe)
├── maodeobra/         # Gestão de mão de obra
├── material/          # Controle de materiais
├── obra/              # Gestão de obras
├── ocorrencia/        # Ocorrências em obras
├── plano/             # Planos de assinatura
├── relatorio/         # Core: Relatórios diários de obra
├── shared/            # Utilitários compartilhados
├── suporte/           # Sistema de tickets de suporte
├── tenant/            # Gestão de empresas (tenants)
├── user/              # Gestão de usuários
├── usertenant/        # Relacionamento usuário-tenant
└── utils/             # Utilitários gerais
```

### Domínios Principais

#### Tenant (Empresa)
Representa uma empresa cadastrada no sistema com seus dados, CNPJ, segmento de atuação e integração com Stripe.

#### Obra
Cada obra possui:
- Informações gerais (nome, contratante, endereço, datas)
- Configurações personalizadas de relatórios
- Controle de status (Em Andamento, Pausada, Concluída)
- Logos customizáveis
- Sistema de grupos para organização

#### Relatório (RDO)
Núcleo do sistema, contém:
- Dados temporais (data início/fim, horários de trabalho)
- Condições climáticas (manhã, tarde, noite)
- Status (Rascunho, Aguardando Aprovação, Aprovado, Rejeitado)
- Mão de obra utilizada
- Equipamentos alocados
- Materiais consumidos
- Atividades realizadas
- Ocorrências registradas
- Fotos e vídeos
- Comentários
- Assinaturas digitais

## Segurança

### Autenticação
- JWT tokens com algoritmo HMAC256
- Refresh tokens para renovação
- `TokenAuthFilter` para validação em cada requisição

### Autorização
- Role-based access control (ADMIN, USER)
- Hierarquia de roles configurada
- Permissões granulares por tenant e obra
- Controle de acesso a itens específicos dos relatórios

### Auditoria
- Envers para histórico completo de alterações
- Registro automático de criador e modificador
- Timestamps de criação e modificação
- Soft delete com flag `ativo`

## Sistema de Pagamentos

### Stripe Integration
- Checkout Sessions para novos planos
- Webhooks para eventos de pagamento
- Gerenciamento de assinaturas recorrentes
- Cobranças automáticas
- Histórico de faturas

### Planos Disponíveis
- Planos com diferentes limites de usuários e obras
- Recorrência: diária, semanal, mensal ou anual
- Planos avulsos (mensal_avulso)
- Período de teste gratuito

### Rotinas Agendadas
- Expiração automática de assinaturas (@Scheduled)

## Gestão de Arquivos

### AWS S3
Estrutura de pastas:
```
tenants/{tenantId}/
  obras/{obraId}/
    relatorios/{relatorioId}/
      fotos/
      videos/
      atividades/{atividadeId}/
        fotos/
users/{userId}/
  foto-perfil-{timestamp}.jpeg
```

### Processamento de Mídia
- **Imagens**: Compressão automática com Thumbnailator
- **Vídeos**: Extração de duração com FFmpeg
- **Formatos suportados**: JPEG, PNG, HEIF, HEIC, MP4, MOV, AVI, etc
- **Limites**: 100MB por arquivo

## Geração de PDFs

O sistema gera PDFs profissionais dos relatórios usando:
1. **Thymeleaf** renderiza o template HTML com os dados
2. **Playwright** converte HTML em PDF com Chromium
3. PDF é salvo no S3 e disponibilizado para download

Template customizável inclui:
- Cabeçalho com logos
- Informações da obra e relatório
- Condições climáticas
- Horários de trabalho
- Todas as seções do relatório
- Fotos e vídeos (com links)
- Assinaturas digitais
- Paginação automática

## Notificações por Email

Templates HTML responsivos para:
- Boas-vindas ao se cadastrar
- Adição de usuário existente a novo tenant
- Criação de novo usuário
- Criação de nova empresa
- Todos enviados via SMTP configurável

## Configuração

### Variáveis de Ambiente Obrigatórias

```yaml
# Database
ENV_DB_URL=jdbc:postgresql://localhost:5432/meudiariodeobras
ENV_DB_USERNAME=root
ENV_DB_PASSWORD=123456

# JWT
ENV_JWT_SECRET=seu-secret-aqui
ENV_JWT_EXPIRATION=86400
ENV_REFRESH_TOKEN_EXPIRATION=604800

# AWS S3
ENV_AWS_ACCESS_KEY=sua-access-key
ENV_AWS_SECRET_KEY=sua-secret-key
ENV_AWS_S3_BUCKET_NAME=seu-bucket

# Email
ENV_MAIL_HOST=smtp.gmail.com
ENV_MAIL_PORT=587
ENV_MAIL_USERNAME=seu-email@gmail.com
ENV_MAIL_PASSWORD=sua-senha
ENV_MAIL_FROM_ADDRESS=noreply@rdodigital.com.br
ENV_MAIL_FROM_NAME=RDO Digital

# Stripe
ENV_STRIPE_API_KEY=sk_test_...
ENV_STRIPE_WEBHOOK_SECRET=whsec_...
ENV_STRIPE_SUCCESS_URL=https://app.rdodigital.com.br/success
ENV_STRIPE_CANCEL_URL=https://app.rdodigital.com.br/cancel

# Profile
ENV_SPRING_PROFILE=default
```

## Como Executar

### Com Docker Compose (Recomendado)

```powershell
# Subir apenas o banco de dados
docker-compose up -d

# Configurar variáveis de ambiente (.env ou configuração do sistema)

# Executar aplicação
./mvnw spring-boot:run
```

### Desenvolvimento Local

```powershell
# Instalar dependências
./mvnw clean install

# Executar aplicação
./mvnw spring-boot:run
```

## API Documentation

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Principais Endpoints

#### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Cadastro

#### Obras
- `GET /api/tenants/{tenantId}/obras` - Listar obras
- `POST /api/tenants/{tenantId}/obras` - Criar obra
- `PUT /api/tenants/{tenantId}/obras/{obraId}` - Atualizar obra

#### Relatórios
- `GET /api/tenants/{tenantId}/relatorios` - Listar relatórios
- `POST /api/tenants/{tenantId}/relatorios` - Criar relatório
- `GET /api/tenants/{tenantId}/relatorios/{relatorioId}` - Detalhes
- `POST /api/tenants/{tenantId}/relatorios/{relatorioId}/pdf/imprimir` - Gerar PDF

#### Fotos e Vídeos
- `POST /api/tenants/{tenantId}/relatorios/{relatorioId}/fotos` - Upload foto
- `POST /api/tenants/{tenantId}/relatorios/{relatorioId}/videos` - Upload vídeo

#### Planos
- `GET /api/planos` - Listar planos
- `POST /api/tenants/{tenantId}/plano-checkout` - Criar checkout

## Tecnologias de Destaque

### Hibernate Envers
Todas as entidades principais utilizam Envers para auditoria completa:
- Quem criou
- Quando foi criado
- Quem modificou
- Quando foi modificado
- Histórico completo de versões

### Soft Delete
Implementado através da flag `ativo` em todas as entidades, permitindo:
- Recuperação de dados deletados
- Auditoria completa
- Manutenção de integridade referencial

### Permissions System
Sistema granular de permissões que controla:
- Acesso a obras específicas
- Visualização de itens dos relatórios
- Edição e aprovação de relatórios
- Gestão de usuários e configurações

## CORS Configuration

Origens permitidas:
- http://localhost:3000 (desenvolvimento)
- https://app.rdodigital.com.br (produção)
- https://www.rdodigital.com.br (produção)

## Licença

Propriedade de Marcus Branches. Todos os direitos reservados.

---

**Versão**: 0.0.1-SNAPSHOT  
**Última atualização**: 2026
