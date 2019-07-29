package Sequencer;

import Utility.Port;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static Utility.Port.REPLICA1_IP;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:32 PM 2019-07-28
 * @Modified by:
 */
public class Sequencer {

    private int sNum;

    public Sequencer() {
        this.sNum = 0;
    }

    public static void main(String[] args) {
        Sequencer sq = new Sequencer();
        sq.listenFromFE();

    }

    private void listenFromFE() {

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Port.SEQUENCER_PORT);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(buffer,
                        buffer.length);
                socket.receive(receivePacket);
                String rm = new String(receivePacket.getData()).trim();
                StringBuilder sb = new StringBuilder();
                sb.append(sNum).append(":").append(rm);
                System.out.println(sb.toString());
                sNum++;

                multiCast(sb.toString(), socket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void multiCast(String msg, DatagramSocket socket) {
        try {
            InetAddress address1 = InetAddress.getByName(REPLICA1_IP);

            DatagramPacket packet1 = new DatagramPacket(msg.getBytes(),
                    msg.length(),
                    address1, Port.REPLICA1_PORT);
            socket.send(packet1);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
