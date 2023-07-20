import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;
import java.net.InetAddress;

public class Player {

    static String nickname;
    static Scanner scan = new Scanner(System.in);
    
    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        DatagramPacket request = null;
        try {
            byte[] buffer = new byte[1000];
            aSocket = new DatagramSocket();

            System.out.print("Bem-vindo ao jogo Pedra - Papel - Tesoura\n Digite seu nickname: ");
            nickname = scan.nextLine();
            byte[] msg = nickname.getBytes();

            InetAddress aHost = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);

            request = new DatagramPacket(msg, msg.length, aHost, serverPort);

            System.out.println("Aguarde seu oponente...");
            aSocket.send(request);
            request = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            String nicknameOpponent = new String(request.getData()).trim();
            System.out.println("Seu oponente é: " + nicknameOpponent);

            int rounds = 0;
            int points = 0;
            int pointsOpponent = 0;

            //Inicia a partida
            while(points < 2 && pointsOpponent <2){
                rounds++;
                int choice = 0;

                //Escolhe uma opção
                while(choice != 1 && choice != 2 && choice != 3){
                    System.out.print(
                    "\nIniciando rodada número [" + rounds +"]\n= Faça sua Escolha =\nPedra (digite 1)\nPapel (digite 2)\nTesoura (digite 3)\nDigite sua escolha: "
                    );
                    choice = scan.nextInt();
                    if(choice != 1 && choice != 2 && choice != 3){
                        System.out.print("\nEscolha uma opção válida!\n");
                    }
                }
               
                //Envia a escolha
                msg = Integer.toString(choice).getBytes();
                request = new DatagramPacket(msg, msg.length, aHost, serverPort);
                aSocket.send(request);

                //Recebe a escolha do oponente
                buffer = new byte[1000];
                request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                int choiceOpponent = Integer.parseInt(new String(request.getData()).trim());
                System.out.println("\nVocê escolheu " + getChoice(choice));
                System.out.println("Seu oponente escolheu " + getChoice(choiceOpponent));

                if ((choice == 1 && choiceOpponent == 3) || (choice == 3 && choiceOpponent == 1) ) {
                    System.out.println("Pedra quebra Tesoura.\n");
                } else if ((choice == 2 && choiceOpponent == 1) || (choice == 1 && choiceOpponent == 2) ) {
                    System.out.println("Papel embrulha Pedra.\n");
                } else if ((choice == 3 && choiceOpponent == 2) || (choice == 2 && choiceOpponent == 3) ) {
                    System.out.println("Tesoura corta Papel.\n");
                } 

                //verifica quem ganhou a rodada
                if (choice == choiceOpponent) {
                    System.out.println("Simbolos Iguais. Empate na rodada!\nNão será atribuido pontos para nenhum dos jogadores.");
                } else if (choice == 1 && choiceOpponent == 3
                        || choice == 2 && choiceOpponent == 1
                        || choice == 3 && choiceOpponent == 2) {
                    System.out.println("Você venceu esta rodada!");
                    points++;
                } else {
                    System.out.println("Você perdeu esta rodada!");
                    pointsOpponent++;
                }

                System.out.println("\n=== PLACAR ===\n" + nickname +": " + points + "\n" + nicknameOpponent +": " + pointsOpponent + "\n=== === === ===");

                //envia quantidade de pontos para o Juiz
                msg = Integer.toString(points).getBytes();
                request = new DatagramPacket(msg, msg.length, aHost, serverPort);
                aSocket.send(request);
            }

            System.out.println("\nTemos um vencedor!\nFim de jogo.");

        } catch (SocketException e) {
            System.out.println("Socket " + e);
        } catch (IOException e){
            System.out.println("IO " + e);
        } finally{
            if(aSocket != null) aSocket.close();
        }
    }

    public static String getChoice(int choice){
        if(choice == 1){
            return "Pedra";
        }
        else  if(choice == 2){
            return "Papel";
        }
        else{
            return "Tesoura";
        }
    }

}
