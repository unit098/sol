package noapplet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A special panel class to provide Applet-like APIs including the life-cycle methods.
 * Define a subclass and call the @link{#run()} method to run it in a JFrame (see the
 * sample code below).
 *
 * <p/>
 * <pre>
 * public class HelloWorld extends NoApplet {
 *
 *   public void paint(Graphics g) {
 *     Dimension d = getSize();
 *     g.setColor(Color.BLACK);
 *     g.fillRect(0, 0, d.width, d.height);
 *     g.setFont(new Font("San-serif", Font.BOLD, 24));
 *     g.setColor(new Color(255, 215,0));
 *     g.drawString("Hello from Rabbit!", 60, 40);
 *     g.drawImage(getImage(getCodeBase(), "image/rabbit.jpg"),
 *       40, 60, this);
 *   }
 *
 *   public static void main(String[] args) {
 *   	new HelloWorld().run();
 *   }
 * }
 * </pre>
 *
 * @author cheon
 */
@SuppressWarnings("serial")
public class NoApplet extends JPanel {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;
    private Map<String, String> parameters = new HashMap<>();

    private JLabel statusBar = new JLabel();

    /** Create a new instance and show it in a frame. */
    protected NoApplet() {
        this(new String[0]);
    }

    /** Create a new instance and show it in a frame.
     * The <code>params</code> parameter behaves like Applet parameters
     * and can be accessed by calling the {@link #getParameter(String)} method.
     * The <code>params</code> parameter is strings of name-value pairs, e.g.,
     *
     * <pre>
     * width=300 height=400 color=red
     * </pre>
     */
    protected NoApplet(String[] params) {
        parseParameters(params);
    }

    /** Called after creation of this NoApplet. */
    public void init() {
    }

    /** Called when the start button is clicked. */
    public void start() {
    }

    /** Called when the stop button is clicked. */
    public void stop() {
    }

    /** Called when this app is closed. */
    public void destroy() {
    }

    /** Return the value of the named parameter or null if the such
     * parameter is defined. */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /** Return the base URL, the directory containing this NoApplet. */
    public URL getCodeBase() {
        return getClass().getResource("/");
    }

    /** Return the document URL, which is the same as the code base URL.
     *
     * @see #getCodeBase()
     */
    public URL getDocumentBase() {
        return getClass().getResource("/");
    }

    /** Return the specified image. The argument must specify an absolute URL.*/
    public Image getImage(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Return the specified image. The argument must specify an absolute URL,
     * and the name is relative to the specified URL. */
    public Image getImage(URL url, String name) {
        try {
            url = new URL(url, name);
            return getImage(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Return the image of the specified file. The file is assumed to be
     * located in the code base.
     *
     * @see #getCodeBase()
     */
    public Image getImage(String file) {
        return getImage(getCodeBase(), file);
    }

    /** Play an audio clip (wav). The file is assumed to be ocated in the code base.
     *
     * @see #getCodeBase()
     */
    public void play(String file) {
        play(getCodeBase(), file);
    }

    /**
     * Play the audio clip (wav) specified by a URL. This method has no effect
     * if the audio clip cannot be found.
     */
    public void play(URL url) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(url);
            //sun.audio.AudioPlayer.player.start(in);
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Play the audio clip (wav) given the URL and a specifier relative to the URL.
     * This method has no effect if the audio clip cannot be found.
     */
    public void play(URL url, String name) {
        try {
            play(new URL(url, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Display the given message in the status window. */
    public void showStatus(String msg) {
        statusBar.setText(msg);
    }

    /** Parse the given parameters, each of the form: <code>name=value</code>. */
    private void parseParameters(String[] params) {
        for (String param: params) {
            StringTokenizer tokens = new StringTokenizer(param, "=");
            if (tokens.hasMoreTokens()) {
                String name = tokens.nextToken().trim().toLowerCase();
                String value = tokens.hasMoreTokens() ? tokens.nextToken().trim() : null;
                parameters.put(name, value);
            }
        }
        int width = parseInt(getParameter("width"), DEFAULT_WIDTH);
        int height = parseInt(getParameter("height"), DEFAULT_HEIGHT);
        setPreferredSize(new Dimension(width, height));
    }

    /** Parse an int value and return it or the default value if the parsed value
     * is negative or a parsing error is encountered.
     */
    private static int parseInt(String value, int defaultValue) {
        try {
            int parsedValue = Integer.parseInt(value);
            return parsedValue <= 0 ? defaultValue : parsedValue;
        } catch (NumberFormatException e) {
        }
        return defaultValue;
    }

    /** Create a button with the given label. */
    private JButton createButton(String label, boolean enabled) {
        JButton button = new JButton(label);
        button.setFocusable(false);
        button.setEnabled(enabled);
        return button;
    }

    /** Create a UI consisting of start and stop buttons. */
    private JPanel createUI() {
        JButton start = createButton("Start", false);
        JButton stop = createButton("Stop", true);
        start.addActionListener(e -> {
            start();
            start.setEnabled(false);
            stop.setEnabled(true);
        });
        stop.addActionListener(e -> {
            stop();
            stop.setEnabled(false);
            start.setEnabled(true);
        });
        JPanel control = new JPanel();
        control.setLayout(new FlowLayout());
        control.add(start);
        control.add(stop);

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.add(control, BorderLayout.NORTH);
        root.add(this, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);
        return root;
    }

    /** Show this NoApplet in a frame with control buttons. */
    public void run() {
        JFrame frame = new JFrame();
        frame.setContentPane(createUI());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                destroy();
            }
        });
        frame.setTitle(getClass().getSimpleName());
        frame.setResizable(false);
        frame.setVisible(true);
        init();
        start();
    }

    public static void main(String[] args) {
        new NoApplet(args).run();
    }
}