import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPTool {
    private JFrame frame;
    private JTextField ipField;
    private JTextField portField;
    private JTextField messageField;
    private JTextArea receiveArea;
    private JButton sendButton;
    private JButton listenButton;
    private DatagramSocket socket;
    private boolean isListening = false;

    public UDPTool() {
        // 创建窗口
        frame = new JFrame("UDP 监听和发送工具");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // IP地址标签和输入框
        JLabel ipLabel = new JLabel("IP地址:");
        ipLabel.setBounds(20, 20, 80, 25);
        frame.add(ipLabel);

        ipField = new JTextField("127.0.0.1");
        ipField.setBounds(100, 20, 150, 25);
        frame.add(ipField);

        // 端口号标签和输入框
        JLabel portLabel = new JLabel("端口号:");
        portLabel.setBounds(20, 60, 80, 25);
        frame.add(portLabel);

        portField = new JTextField("8000");
        portField.setBounds(100, 60, 150, 25);
        frame.add(portField);

        // 消息输入框
        JLabel messageLabel = new JLabel("消息:");
        messageLabel.setBounds(20, 100, 80, 25);
        frame.add(messageLabel);

        messageField = new JTextField();
        messageField.setBounds(100, 100, 150, 25);
        frame.add(messageField);

        // 发送按钮
        sendButton = new JButton("发送UDP消息");
        sendButton.setBounds(20, 140, 150, 25);
        frame.add(sendButton);

        // 监听按钮
        listenButton = new JButton("开始监听");
        listenButton.setBounds(180, 140, 150, 25);
        frame.add(listenButton);

        // 接收消息显示区域
        receiveArea = new JTextArea();
        receiveArea.setBounds(20, 180, 450, 150);
        receiveArea.setLineWrap(true);
        receiveArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiveArea);
        scrollPane.setBounds(20, 180, 450, 150);
        frame.add(scrollPane);

        // 绑定按钮事件
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendUDP();
            }
        });

        listenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isListening) {
                    stopListening();
                } else {
                    startListening();
                }
            }
        });

        frame.setVisible(true);
    }

    // 发送UDP消息
    private void sendUDP() {
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            String message = messageField.getText();

            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

            socket.send(packet);
            receiveArea.append("发送消息: " + message + "\n");
            socket.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "发送失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 开始监听UDP消息
    private void startListening() {
        try {
            int port = Integer.parseInt(portField.getText());
            socket = new DatagramSocket(port);

            isListening = true;
            listenButton.setText("停止监听");
            receiveArea.append("正在监听端口: " + port + "\n");

            // 开启新线程来处理UDP接收
            new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (isListening) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String received = new String(packet.getData(), 0, packet.getLength());
                        receiveArea.append("接收到消息: " + received + "\n");
                    }
                } catch (Exception ex) {
                    if (isListening) {
                        JOptionPane.showMessageDialog(frame, "监听失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }).start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "监听失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 停止监听UDP消息
    private void stopListening() {
        isListening = false;
        listenButton.setText("开始监听");
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        receiveArea.append("监听已停止\n");
    }

    public static void main(String[] args) {
        new UDPTool();
    }
}