# ДАН — локал орчны товч командууд
.PHONY: up down logs ready client clean discovery

up:            ## Орчныг асаах (Hydra + Postgres + Redis)
	docker compose up -d

down:          ## Орчныг унтраах
	docker compose down

clean:         ## Орчныг унтрааж бүх өгөгдлийг устгах
	docker compose down -v

logs:          ## Hydra-гийн логийг харах
	docker compose logs -f hydra

ready:         ## Hydra бэлэн эсэхийг шалгах
	@curl -fsS http://localhost:4445/health/ready && echo " ✓ Hydra бэлэн"

discovery:     ## OIDC discovery баримтыг харах
	@curl -fsS http://localhost:4444/.well-known/openid-configuration | jq .

client:        ## Туршилтын үйлчилгээ (RP) бүртгэх
	@bash scripts/register-client.sh

help:          ## Командуудыг харуулах
	@grep -E '^[a-z]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-12s\033[0m %s\n",$$1,$$2}'
