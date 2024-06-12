#!/bin/bash

# Atualizar a lista de pacotes disponíveis
echo "Atualizando a lista de pacotes..."
if ! sudo apt update; then
    echo "Falha ao atualizar a lista de pacotes."
    exit 1
fi

# Atualizar os pacotes instalados
echo "Atualizando os pacotes instalados..."
if ! sudo apt upgrade -y; then
    echo "Falha ao atualizar os pacotes instalados."
    exit 1
fi

# Trocar a senha do root
echo "Trocando a senha do usuário root..."
echo "root:urubu100" | sudo chpasswd || { echo "Falha ao trocar a senha do root."; exit 1; }

# Trocar a senha do usuário ubuntu
echo "Trocando a senha do usuário ubuntu..."
if id "ubuntu" &>/dev/null; then
    echo "ubuntu:urubu100" | sudo chpasswd || { echo "Falha ao trocar a senha do usuário ubuntu."; exit 1; }
    echo "Senha do usuário ubuntu trocada com sucesso."
else
    echo "Usuário ubuntu não encontrado."
fi

echo "Atualização e troca de senhas concluídas com sucesso."

# Verifica se o Java está instalado
echo "Verificando se o Java está instalado..."
if ! java -version &>/dev/null; then
    echo "Java não instalado. Instalando o Java..."
    if ! sudo apt install openjdk-17-jre -y; then
        echo "Falha na instalação do Java. Verifique os logs para mais informações."
        exit 1
    fi
    echo "Java instalado com sucesso!"
else
    echo "Java já está instalado."
fi

# Importa bibliotecas após a instalação do Java
echo "Importando bibliotecas..."
cd ~
if ! git clone https://github.com/LiSync-Signage/JAR-Individual.git; then
    echo "Falha ao importar bibliotecas. Verifique os logs para mais informações."
    exit 1
fi
echo "Bibliotecas importadas com sucesso!"

# Instalação do Docker e configuração do MySQL
# Atualizar a lista de pacotes disponíveis
echo "Atualizando a lista de pacotes novamente..."
if ! sudo apt update; then
    echo "Falha ao atualizar a lista de pacotes."
    exit 1
fi

# Instalar o Docker
echo "Instalando o Docker..."
if ! sudo apt install docker.io -y; then
    echo "Falha na instalação do Docker. Verifique os logs para mais informações."
    exit 1
fi

# Iniciar o serviço Docker
echo "Iniciando o serviço Docker..."
if ! sudo systemctl start docker; then
    echo "Falha ao iniciar o serviço Docker. Verifique os logs para mais informações."
    exit 1
fi

# Habilitar o Docker para iniciar com o sistema operacional
echo "Habilitando o Docker para iniciar com o sistema operacional..."
if ! sudo systemctl enable docker; then
    echo "Falha ao habilitar o Docker para iniciar com o sistema operacional. Verifique os logs para mais informações."
    exit 1
fi

# Fazer pull da imagem do MySQL 5.7 do Docker Hub
echo "Baixando a imagem do MySQL 5.7..."
if ! sudo docker pull mysql:5.7; then
    echo "Falha ao baixar a imagem do MySQL. Verifique os logs para mais informações."
    exit 1
fi

# Criar e iniciar o container MySQL
echo "Criando e iniciando o container MySQL..."
if ! sudo docker run -d -p 3306:3306 --name ContainerBD -e MYSQL_DATABASE=lisyncDB -e MYSQL_ROOT_PASSWORD=urubu100 mysql:5.7; then
    echo "Falha ao criar o container MySQL. Verifique os logs para mais informações."
    exit 1
fi

# Verificar se o container foi criado com sucesso
echo "Verificando o status do container..."
container_status=$(sudo docker ps -a --filter "name=ContainerBD" --format "{{.Status}}")

if [[ $container_status == Up* ]]; then
    echo "O container foi criado com sucesso e está em execução."

    # Avisar o usuário sobre a espera
    echo "Aguardando 30 segundos para o MySQL iniciar completamente..."
    sleep 30

    # Criar o usuário marcelo e conceder todos os privilégios a ele
    echo "Criando o usuário 'marcelo' e concedendo privilégios..."
    if ! sudo docker exec -i ContainerBD mysql -u root -purubu100 -e "CREATE USER 'marcelo'@'%' IDENTIFIED BY 'urubu100'; ALTER USER 'marcelo' IDENTIFIED WITH mysql_native_password BY 'urubu100'; GRANT ALL PRIVILEGES ON *.* TO 'marcelo'@'%'; FLUSH PRIVILEGES;"; then
        echo "Falha ao criar o usuário 'marcelo'. Verifique os logs para mais informações."
        exit 1
    fi
    echo "Usuário 'marcelo' criado com sucesso e privilégios concedidos."

    # Executar o script SQL "lisyncBD"
    echo "Executando o script SQL 'lisyncBD'..."
    if ! sudo docker exec -i ContainerBD sh -c 'mysql -u root -purubu100 lisyncDB < /home/ubuntu/JAR-Individual/Marcelo/recursos/lisyncDBNew.sql'; then
        echo "Falha ao executar o script SQL. Verifique os logs para mais informações."
        exit 1
    fi
    echo "Script SQL 'lisyncBD' executado com sucesso."
else
    echo "A criação do container falhou. Verificando os logs..."
    sudo docker logs ContainerBD
    exit 1
fi

echo "Script concluído com sucesso."
