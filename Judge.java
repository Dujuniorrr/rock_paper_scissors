import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.net.InetAddress;

public class Judge {

    static int portNumber = 6789;
    static ArrayList<String> nicknamesOfPlayers = new ArrayList<String>();
    static ArrayList<InetAddress> addressOfPlayer = new ArrayList<InetAddress>();
    static ArrayList<Integer> portsOfPlayer = new ArrayList<Integer>();

    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        DatagramPacket request = null;
        try {
            byte[] buffer = new byte[1000];
            aSocket = new DatagramSocket(portNumber);
            System.out.println("\nInicializando Juiz do jogo Pedra - Papel - Tesoura\nAguardando cadastro de jogador...");

            // Cadastrando jogadores
            while(addressOfPlayer.size() < 2){
                buffer = new byte[1000];
                request = new DatagramPacket(buffer, buffer.length); 
                aSocket.receive(request);

                nicknamesOfPlayers.add(new String(request.getData()).trim());
                addressOfPlayer.add(request.getAddress());
                portsOfPlayer.add(request.getPort());

                System.out.println("Jogador " + addressOfPlayer.size() +" (" + nicknamesOfPlayers.get( addressOfPlayer.size() -1) + ") cadastrado.");
            }

            int player = 0;  // 0 -> 1
            int opponent = 1; // 1 -> 0

            // Enviando oponentes
            while(player <= 1){ 
                byte[] msg = nicknamesOfPlayers.get(opponent).getBytes(); // n2
                request = new DatagramPacket(msg, msg.length, addressOfPlayer.get(player), portsOfPlayer.get(player));
                aSocket.send(request);
                player++;
                opponent--;
            }

            //Inicia a partida
            int points = 0;
            while(points < 2){
                for(int i = 0; i < 2; i++){
                    buffer = new byte[1000];
                    request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request); 
                    String choice = new String(request.getData()).trim();
            
                    //Envia as escolhas para os jogadores
                    for(int j = 0; j < addressOfPlayer.size(); j++){
                        if(!request.getAddress().equals(addressOfPlayer.get(j)) || request.getPort() != portsOfPlayer.get(j).intValue()){
                            byte[] msg = choice.getBytes();
                            DatagramPacket requestChoiceOpponent = new DatagramPacket(msg, msg.length, addressOfPlayer.get(j), portsOfPlayer.get(j));
                            aSocket.send(requestChoiceOpponent);
                        }
                    }
                }

                //verifica se há um vencedor
                for(int i = 0; i < 2; i++){
                    buffer = new byte[1000];
                    request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request); 
                    points = Integer.parseInt(new String(request.getData()).trim());
                    if(points > 1){
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
