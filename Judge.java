import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.net.InetAddress;

public class Judge {

    public static void main(String[] args) {

        int portNumber = 6789; // porta do processo

        ArrayList<String> nicknamesOfPlayers = new ArrayList<String>(); // lista dos nicksnames 
        ArrayList<InetAddress> addressOfPlayers = new ArrayList<InetAddress>(); // lista dos ips
        ArrayList<Integer> portsOfPlayers = new ArrayList<Integer>(); // lista das portas

        DatagramSocket aSocket = null; // socket
        DatagramPacket request = null; // envia e recebe mensagem

        try {

            byte[] buffer = new byte[1000]; // necessário para salvar os dados da mensagem
            aSocket = new DatagramSocket(portNumber); // instancia o socket, e ele faz o bind explicito

            System.out.println("\nInicializando Juiz do jogo Pedra - Papel - Tesoura\nAguardando cadastro de jogador...");

            // Cadastrando jogadores
            while(addressOfPlayers.size() < 2){
                buffer = new byte[1000]; // limpa os bytes 
                request = new DatagramPacket(buffer, buffer.length); // prepara para o recebimento da mensagem
                aSocket.receive(request); // espera a mensaggem chegar

                nicknamesOfPlayers.add(new String(request.getData()).trim()); // adiciona na lista dos nicksnames 
                addressOfPlayers.add(request.getAddress()); // adiciona na lista dos ips 
                portsOfPlayers.add(request.getPort()); // adiciona na lista das portas

                System.out.println("Jogador " + addressOfPlayers.size() +" (" + nicknamesOfPlayers.get( addressOfPlayers.size() -1) + ") cadastrado."); // printa para o usuário
            }

            int player = 0;  // 0 -> 1 - incrementar
            int opponent = 1; // 1 -> 0 - decrementar

            // Enviando oponentes
            while(player <= 1){ 

                byte[] msg = nicknamesOfPlayers.get(opponent).getBytes(); // pega o nickname do oponente em bytes

                request = new DatagramPacket(msg, msg.length, addressOfPlayers.get(player), portsOfPlayers.get(player)); // prepara para enviar a mensagem

                aSocket.send(request); //envia

                player++; // jogador incrementa
                opponent--; // oponente decrementa
            }

            //Inicia a partida
            int points = 0;

            while(points < 2){ // enquanto nenhum jogador tiver mais de 1 ponto, a partida continua

                for(int i = 0; i < 2; i++){ // pegar as escolhas dos jogadores

                    buffer = new byte[1000]; //limpa os bytes para receber a escolha

                    //pega a escolha
                    request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request); 

                    // transforma em string
                    String choice = new String(request.getData()).trim();
            
                    //Envia as escolhas para os jogadores
                    for(int j = 0; j < addressOfPlayers.size(); j++){ //repete duas vezes


                        // pega o oponente do jogador que enviou a mensagem
                        if(!request.getAddress().equals(addressOfPlayers.get(j)) || request.getPort() != portsOfPlayers.get(j).intValue()){

                            //envia a escolha para o oponente
                            byte[] msg = choice.getBytes();
                            DatagramPacket requestChoiceOpponent = new DatagramPacket(msg, msg.length, addressOfPlayers.get(j), portsOfPlayers.get(j));
                            aSocket.send(requestChoiceOpponent);

                        }

                    }
                }

                //verifica se há um vencedor
                for(int i = 0; i < 2; i++){ //repete duas vezes

                    buffer = new byte[1000]; //limpa os bytes

                    request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request); 
                    points = Integer.parseInt(new String(request.getData()).trim()); // recebe a pontuação

                    if(points > 1){ //valor do ponto for maior que 2, ele para o loop, pois há um vencedor
                        break;
                    }

                }
            }
        } catch (SocketException e) {
            System.out.println("Socket " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO " + e.getMessage());
        } finally {
            if(aSocket != null) aSocket.close();
        }

    }
}
