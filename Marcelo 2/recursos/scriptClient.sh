#!/bin/bash

# Atualizar a lista de pacotes disponíveis
echo "Atualizando a lista de pacotes..."
sudo apt update

# Atualizar os pacotes instalados
echo "Atualizando os pacotes instalados..."
sudo apt upgrade -y

# Trocar a senha do root
echo "Trocando a senha do usuário root..."
echo "root:urubu100" | sudo chpasswd

# Trocar a senha do usuário ubuntu
echo "Trocando a senha do usuário ubuntu..."
if id "ubuntu" &>/dev/null; then
    echo "ubuntu:urubu100" | sudo chpasswd
    echo "Senha do usuário ubuntu trocada com sucesso."
else
    echo "Usuário ubuntu não encontrado."
fi

echo "Atualização e troca de senhas concluídas com sucesso."

# Verifica se o Java está instalado
java -version
if [ $? = 0 ]; then
    echo "Java instalado."
else
    echo "Java não instalado."
    echo "Instalando o Java..."
    sudo apt install openjdk-17-jre -y
    # Verifica se a instalação foi bem-sucedida
    if [ $? = 0 ]; then
        echo "Java instalado com sucesso!"
    else
        echo "Falha na instalação do Java. Verifique os logs para mais informações."
        exit 1
    fi
fi

# Importa bibliotecas após a instalação do Java
echo "Importando bibliotecas..."
cd ~
git clone https://github.com/LiSync-Signage/JAR-Individual.git
# Verifica se o clone foi bem-sucedido
if [ $? = 0 ]; then
    echo "Bibliotecas importadas com sucesso!"
else
    echo "Falha ao importar bibliotecas. Verifique os logs para mais informações."
    exit 1
fi

# Instalação do Docker e configuração do MySQL
# Atualizar a lista de pacotes disponíveis
echo "Atualizando a lista de pacotes..."
sudo apt update

# Instalar o Docker
echo "Instalando o Docker..."
sudo apt install docker.io -y

# Iniciar o serviço Docker
echo "Iniciando o serviço Docker..."
sudo systemctl start docker

# Habilitar o Docker para iniciar com o sistema operacional
echo "Habilitando o Docker para iniciar com o sistema operacional..."
sudo systemctl enable docker

# Fazer pull da imagem do MySQL 5.7 do Docker Hub
echo "Baixando a imagem do MySQL 5.7..."
sudo docker pull mysql:5.7

# Criar e iniciar o container MySQL
echo "Criando e iniciando o container MySQL..."
sudo docker run -d -p 3306:3306 --name ContainerBD -e MYSQL_DATABASE=lisyncDB -e MYSQL_ROOT_PASSWORD=urubu100 mysql:5.7

# Verificar se o container foi criado com sucesso
echo "Verificando o status do container..."
sudo docker ps -a

# Verificar o status do container
container_status=$(sudo docker ps -a --filter "name=ContainerBD" --format "{{.Status}}")

if [[ $container_status == Up* ]]; then
    echo "O container foi criado com sucesso e está em execução."

    # Avisar o usuário sobre a espera
    echo "Aguardando 30 segundos para o MySQL iniciar completamente..."
    sleep 30

    # Criar o usuário marcelo e conceder todos os privilégios a ele
    echo "Criando o usuário 'marcelo' e concedendo privilégios..."
    sudo docker exec -i ContainerBD mysql -u root -p urubu100 -e "CREATE USER 'marcelo'@'%' IDENTIFIED BY 'urubu100'; ALTER USER 'marcelo' IDENTIFIED WITH mysql_native_password BY 'urubu100'; GRANT ALL PRIVILEGES ON *.* TO 'marcelo'@'%'; FLUSH PRIVILEGES;"

    echo "Usuário 'marcelo' criado com sucesso e privilégios concedidos."

    # Executar o script SQL "lisyncBD"
    echo "Executando o script SQL 'lisyncBD'..."
    sudo docker exec -i ContainerBD sh -c 'mysql -u root -p urubu100 bancoLocal < /home/ubuntu/JAR-Individual/Marcelo/recursos/lisyncDBNew.sql'

    echo "Script SQL 'lisyncBD' executado com sucesso."
else
    echo "A criação do container falhou. Verificando os logs..."
    sudo docker logs ContainerBD
fi
