import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
//class GetTheCurrentTime {
//public Date getTime( ) {
// This Class return the time of delivered picket on Second/ Day/ Month and year // one way
//long currentTimeInMillis =System.currentTimeMillis();
//Date today = new Date( currentTimeInMillis );// System.out.println( today ); // another way
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
//for voice
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

//for current Date and Time: 
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
class GetTheCurrentTime
{
    public String getTime()
    {

    // This Class return the time of delivered picket on Second/ Day/ Month and year
    // one way
    /*long currentTimeInMillis =System.currentTimeMillis();
    Date today = new Date( currentTimeInMillis );
    // System.out.println( today );
    // another way
    today = cal.getTime();
    return today;
    // System.out.println( today );
    */

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
}
public class TestVoiceChat
{
    String mes1,mes;
    /* This class add many Buttons, Textarea for the Client/ Server User Interface */
    Thread thread;
    MulticastSocket socket;
    InetAddress add;
    JFrame frame;   
    JPanel panel,panel2;
    JTextArea area,area2,area3,area4,area5;
    JScrollPane pane,pane2;
    JLabel label,label2,label3,label4,label5,label6;
    JButton button,button1,button2,button6;
    // for voice
    //define socket
    DatagramSocket server_socket;
    //audio
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    SourceDataLine sourceDataLine;
    AudioInputStream audioInputStream;
    public static void main(String[] args) 
    {
        if ( args.length == 0)
        {
             TestVoiceChat u = new TestVoiceChat();
        }
        else
        System.exit(0);
    }
    public TestVoiceChat()
    {
        frame = new JFrame("****Confab Application****");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        panel = new JPanel();
        panel.setLayout(null);
        button1 = new JButton("Connect");
        button6 = new JButton("Voice"); 
        button1.setBounds(273, 110,115,40);
        button6.setBounds(273, 210,115,40);
        button6.setEnabled(false);
        label3 = new JLabel("IPAddress:");
        label3.setBounds(273, 255,100, 20);
        Icon image = new ImageIcon( "Test.jpeg" );
        label6 = new JLabel("ftcnty",image, SwingConstants.LEFT);
        label6.setBounds(30, 450, 150,150);
        area3 = new JTextArea("255.255.255.255");
        area3.setBounds(273, 275,100, 20);
        label4 = new JLabel("Sending port:");
        label4.setBounds(273, 300,100, 20);
        area4 = new JTextArea("5000");
        area4.setBounds(273, 320,100, 20);
        label5 = new JLabel("Receiving port:");
        label5.setBounds(273, 345,100, 20);
        area5 = new JTextArea("6000");
        area5.setBounds(273, 365,100, 20);
        button1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            new StartServer();
            button6.setEnabled(true);
            }
        });
        button6.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            // Start Voice Code
            captureAudio();
            button6.setEnabled(false);
            }
        });
            panel.add(button1);
            panel.add(button6);
            panel.add(label3);
            panel.add(label4);
            panel.add(label5);
            panel.add(label6);
            panel.add(area3);
            panel.add(area4);
            panel.add(area5);
            button2 = new JButton("Terminate");
            button2.setBounds(273, 160, 115,40);


            button2.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    thread.interrupt();
                    socket.close();
                    area2.append("Server is stopped\n");
                    button1.setEnabled(true);
                    button2.setEnabled(false);
                }
            });
            button2.setEnabled(false);
            panel.add(button2);
            label = new JLabel("Type your message here : ");
            label.setBounds(15, 210, 120, 360);
            panel.add(label);
            label2 = new JLabel("Chat Window");
            label2.setBounds(10, 10, 150, 20); panel.add(label2);
            area2 = new JTextArea();
            pane2 = new JScrollPane(area2);
            pane2.setBounds(10, 30, 260, 350);
            panel.add(pane2);
            area = new JTextArea();
            pane = new JScrollPane(area);
            pane.setBounds(10, 400, 260, 40);
            panel.add(pane);
            button = new JButton("Send");
            button.setBounds(273,400, 115, 40);

            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    new SendRequest(); 
                }
            });


            panel.add(button);
            frame.add(panel);
            frame.setSize(410, 650);
            frame.setVisible(true);
            frame.setLocation(300, 50);
    }

public class StartServer implements Runnable
{
    StartServer()
    {
        thread = new Thread(this);
        thread.start();
        button1.setEnabled(false);
        button2.setEnabled(true);
    }
    public void run()
    {
    // Server side that receive the message
    try
    {
        socket = new MulticastSocket(6789);
        add = InetAddress.getByName("224.0.0.0");
        socket.joinGroup(add);
        area2.append("Server is started\n");
        while(true)
        {
            try
            {
                //Receive request from client
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, add, 6789);
                socket.receive(packet);
                String addressclint= packet.getAddress().toString();
                //String
                mes1 = new String(buffer).trim() ;
                // String
                GetTheCurrentTime GCT=new GetTheCurrentTime();
                // Date mm=GCT.getTime();
                area2.append(addressclint +" :"+mes1+ "\n");
                area2.append(GCT.getTime()+" \n\n ");
            }
            catch(UnknownHostException ue){}
        }
    }
    catch(IOException e){}
}
}
public class SendRequest
{ //inclass
SendRequest()
{
// client side that send the packets
try
{
    add = InetAddress.getByName("224.0.0.0");  
    MulticastSocket socket = new
    MulticastSocket();
    socket.joinGroup(add);
    byte[] buffer = new byte[65535];
    String mess = area.getText();
    buffer = mess.getBytes();
    DatagramPacket packet = new
    DatagramPacket(buffer, buffer.length, add, 6789);
    socket.send(packet);
    area.setText("");
    socket.close();
}
catch(IOException io){}
}
}
/////////////////////////////////////////////////////
///////////////
// voice Classes
// first function called by main
public void captureAudio() 
{
try
{
//print audio devices informatioin
    Mixer.Info[] mixerInfo =
    AudioSystem.getMixerInfo();
    System.out.println("Available mixers:");
    for (int cnt = 0; cnt < mixerInfo.length; cnt++)
    {
        System.out.println(mixerInfo[cnt].getName());
    }
// get audio from mic X
    audioFormat = getAudioFormat();
    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);
    targetDataLine = (TargetDataLine)mixer.getLine(dataLineInfo);
    targetDataLine.open(audioFormat);
    targetDataLine.start();
// call thread to send audio
    Thread captureThread = new CaptureThread();
    captureThread.start();
// send audio to speaker X
    DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
    sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo1);
    sourceDataLine.open(audioFormat);
    sourceDataLine.start();
// call thread to recive audio
    Thread playThread = new PlayThread();
    playThread.start();
}
catch (Exception e)
{
    System.out.println(e);
    System.exit(0);
}
}
// sending audio to server
class CaptureThread extends Thread
{
    byte tempBuffer[] = new byte[1024];
    public void run()
    {
        try
        {
            DatagramSocket client_socket = new DatagramSocket();
            InetAddress IPAddress =InetAddress.getByName( area3.getText());
            while (true)
            {
                int cnt =targetDataLine.read(tempBuffer, 0,tempBuffer.length);
                DatagramPacket send_packet = new DatagramPacket(tempBuffer, tempBuffer.length,IPAddress,Integer.valueOf( area4.getText()));
                client_socket.send(send_packet);
            }
        } 
        catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }
}
// coding and format audio
private AudioFormat getAudioFormat()
{
    float sampleRate = 8000.0F;
    int sampleSizeInBits = 16;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;
    return new AudioFormat(sampleRate,sampleSizeInBits, channels, signed, bigEndian);
}
// recieving audio from server and play it
class PlayThread extends Thread 
{
    byte tempBuffer[] = new byte[1024];
    public void run() 
    {
        try
        {
            DatagramSocket server_socket = new DatagramSocket(Integer.valueOf( area5.getText()));
            while (true)
            {
                DatagramPacket receive_packet = new DatagramPacket(tempBuffer,tempBuffer.length);
                server_socket.receive(receive_packet);
                sourceDataLine.write(receive_packet.getData(), 0,tempBuffer.length);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
}
