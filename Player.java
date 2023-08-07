import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;
import java.net.InetAddress;

public class Player {
    
    public static void main(String[] args) {

        String nickname; // salva o nickname
        Scanner scan = new Scanner(System.in); //instancia o scanner para pegar as entradas do usuário

        DatagramSocket aSocket = null;
        DatagramPacket request = null;

        try {
            byte[] buffer = new byte[1000]; //limpo os bytes
            aSocket = new DatagramSocket(); //crio o socket, e faz um bind implicito

            System.out.print("Bem-vindo ao jogo Pedra - Papel - Tesoura\n Digite seu nickname: ");
            nickname = scan.nextLine();
            byte[] msg = nickname.getBytes(); //pega o nickname

            //pega o ip e a porta do juiz que foram adicionados no terminal
            InetAddress aHost = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]); 

            //prepara para enviar o nickname
            request = new DatagramPacket(msg, msg.length, aHost, serverPort);

            //envia o nickname
            System.out.println("Aguarde seu oponente...");
            aSocket.send(request);

            //prepara para receber o nickname do oponente
            request = new DatagramPacket(buffer, buffer.length);
            //recebe o nickname
            aSocket.receive(request);

            //printa o nickname do oponente
            String nicknameOpponent = new String(request.getData()).trim();
            System.out.println("Seu oponente é: " + nicknameOpponent);

            //crio as variaveis de round, pontos do jogador e do oponente
            int rounds = 0;
            int points = 0;
            int pointsOpponent = 0;

            //Inicia a partida
            while(points < 2 && pointsOpponent < 2){ //enquanto nenhum jogador fazer 2 pontos, a partida continua

                rounds++; //a rodada incrementa
                int choice = 0; //e a escolha volta para zero

                //Escolhe uma opção, deve ser igual 1, 2 ou 3
                while(choice != 1 && choice != 2 && choice != 3){

                    System.out.print(
                    "\nIniciando rodada número [" + rounds +"]\n= Faça sua Escolha =\nPedra (digite 1)\nPapel (digite 2)\nTesoura (digite 3)\nDigite sua escolha: "
                    );

                    choice = scan.nextInt();

                    if(choice != 1 && choice != 2 && choice != 3){
                        System.out.print("\nEscolha uma opção válida!\n");
                    }

                }
               
                //Envia a escolha do usuario
                msg = Integer.toString(choice).getBytes();
                request = new DatagramPacket(msg, msg.length, aHost, serverPort);
                aSocket.send(request);

                //Recebe a escolha do oponente
                buffer = new byte[1000];
                request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                int choiceOpponent = Integer.parseInt(new String(request.getData()).trim());

                //printa as escolhas
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
                    System.out.println("\nSimbolos Iguais. Empate na rodada!\nNão será atribuido pontos para nenhum dos jogadores.");
                    //se nenhum ganhou, ninguem ganha ponto
                } else if (choice == 1 && choiceOpponent == 3
                        || choice == 2 && choiceOpponent == 1
                        || choice == 3 && choiceOpponent == 2) {
                    System.out.println("Você venceu esta rodada!");
                    points++; //se o jogador ganhou, ele ganha mais um ponto 
                } else {
                    System.out.println("Você perdeu esta rodada!");
                    pointsOpponent++; //se o oponente ganhou, ele que ganha mais um ponto
                }

                //mostra o placar
                System.out.println("\n=== PLACAR ===\n" + nickname +": " + points + "\n" + nicknameOpponent +": " + pointsOpponent + "\n=== === === ===");

                //envia quantidade de pontos para o Juiz
                msg = Integer.toString(points).getBytes();
                request = new DatagramPacket(msg, msg.length, aHost, serverPort);
                aSocket.send(request);

                //se alguem fez dois pontos, já sai do laço
            }

            //ocorre depois que sai do laço
            System.out.println("\nTemos um vencedor!\nFim de jogo.");

        } catch (SocketException e) {
            System.out.println("Socket " + e);
        } catch (IOException e){
            System.out.println("IO " + e);
        } finally{
            if(aSocket != null) aSocket.close(); scan.close();
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
