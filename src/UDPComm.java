import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPComm {
    String host = new String();
    int port;
    char[] msg = new char[256];
    byte[] msgByte = new byte[256];

    public boolean sendMsg() {
        try {
            // envia msg para destino
            InetAddress addr = InetAddress.getByName(host);
            // monta o pacote a ser enviado
            DatagramPacket pkg = new DatagramPacket(charToByte(msg),msg.length, addr, port);
            // cria o DatagramSocket que será responsável por enviar a mensagem
            DatagramSocket ds = new DatagramSocket();
            // envia a mensagem
            ds.send(pkg);
            ds.close();
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public boolean receiveMsg() {
        byte[] msgByte = new byte[256];
        try {
            // recebe msgByte
            DatagramSocket ds = new DatagramSocket(port);
            // prepara o pacote de dados
            DatagramPacket pkg = new DatagramPacket(msgByte, msgByte.length);
            // recebimento da mensagem
            ds.receive(pkg);
            // ajusta host de remessa
            this.host = pkg.getAddress().getHostName();
            // ajusta mensagem recebida
            setMsg(pkg.getData());
            ds.close();
            return true;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }
    }

    public void setMsg(byte[] msgByte){
        this.msgByte = msgByte;
        this.msg = getMsg();
    }

    public char[] getMsg(){
        String msgStr = "";
        for (int i = 0; i < this.msgByte.length; i++){
            if (this.msgByte[i] != 0)
                msgStr = msgStr + this.msgByte[i];
        }
        return msgStr.toCharArray();
    }

    public char[] getJogada(){
        char[] jogada = {' ',' ',' ',' ',' ',' ',' ',' ',' '};
        for (int i=0;i<this.msg.length;i++) {
            if (i > 8) break;
            if (this.msg[i] != 0) {
                jogada[i] = this.msg[i];
            }
        }
        return jogada;
    }

    public UDPComm(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public UDPComm(int port) {
        this.port = port;
    }

    private byte[] charToByte(char[] msg){
        byte[] msgByte = new byte[msg.length];
        for (int i = 0; i < msg.length; i++)
            msgByte[i] = (byte) msg[i];
        return msgByte;
    }

    private char[] byteToChar(byte[] msgByte){
        char[] msg = new char[msgByte.length];
        for (int i = 0; i < msgByte.length; i++)
            msg[i] = (char) msgByte[i];
        return msg;
    }
}
