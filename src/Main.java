import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import spacecore.PlayerShip;
import spacecore.UserInterface;
import spacecore.World;

// Simple main application entry point
public class Main
{
    // Default settings
    public static final int DISPLAY_HEIGHT = 900;
    public static final int DISPLAY_WIDTH = 1400;
    
    // Renderable items
    PlayerShip TestShip;
    World TestWorld;
    UserInterface UI;
    
    // Debug var
    float Time;
    
    // Ship / camera variables
    Vector3f CameraPos = new Vector3f();
    Vector3f CameraTarget = new Vector3f();
    Vector3f CameraUp = new Vector3f();
    
    // Camera state
    boolean CameraType = false;
    
    public static void main(String[] args) {
        Main main = null;
        try {
            System.out.println("Keys:");
            System.out.println("down  - Shrink");
            System.out.println("up    - Grow");
            System.out.println("left  - Rotate left");
            System.out.println("right - Rotate right");
            System.out.println("esc   - Exit");
            main = new Main();
            main.create();
            main.run();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.toString());
        } finally {
            if (main != null) {
                main.destroy();
            }
        }
    }

    public Main() {
        // Do nothing...
    }

    public void create() throws LWJGLException {
        
        //Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setVSyncEnabled(true);
        Display.setFullscreen(false);
        Display.setTitle("SpaceCore 0.1 - CoreS2 Software Solutions");
        Display.create();
        
        //Keyboard
        Keyboard.create();
        
        //Mouse
        Mouse.setGrabbed(false);
        Mouse.create();
        
        //OpenGL
        initGL();
        resizeGL();
        
        // Create our world and ships
        TestWorld = new World();
        TestShip = new PlayerShip();
        UI = new UserInterface();
        
        // Setup fog
        glFogi(GL_FOG_MODE, GL_EXP);
        //glFogfv(GL_FOG_COLOR, fogColor);
        glFogf(GL_FOG_DENSITY, 0.01f);
        glHint(GL_FOG_HINT, GL_DONT_CARE);
        glFogf(GL_FOG_START, World.SkyboxSize);
        glFogf(GL_FOG_END, World.SkyboxSize * 2);
        glEnable(GL_FOG);
    }
    
    public void destroy() {
        //Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void initGL() {
        //2D Initialization
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black
        glDisable(GL_DEPTH_TEST);
    }
    
    // 2D mode
    public void resizeGL2D()
    {
        // 2D Scene
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0.0f, (float)DISPLAY_WIDTH, (float)DISPLAY_HEIGHT, 0.0f);
        glMatrixMode(GL_MODELVIEW);
        
        // Set depth buffer elements
        glDisable(GL_DEPTH_TEST);
    }
    
    // 3D mode
    public void resizeGL()
    {
        // 3D Scene
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45.0f, ((float) DISPLAY_WIDTH / (float) DISPLAY_HEIGHT), 0.1f, 100.0f);
        glMatrixMode(GL_MODELVIEW);
        
        // Set depth buffer elements
        glClearDepth(1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }
    
    public void run()
    {
        // Keep looping until we hit a quit event
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                update();
                render();
            } else {
                if (Display.isDirty()) {
                    render();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            Display.update();
            Display.sync(60);
        }
    }

    public void render()
    {
        // Clear screen and load up the 3D matrix state
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        
        // 3D render
        resizeGL();
        
        // Move camera to right behind the ship
        //public static void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx, float upy, float upz)
        Time += 0.001f;
        float CDist = 6;
        
        // Set the camera on the back of the 
        TestShip.GetCameraVectors(CameraPos, CameraTarget, CameraUp);
        
        // Tail-plane camera
        if(CameraType)
        {
            // Extend out the camera by length
            Vector3f Dir = new Vector3f();
            Vector3f.sub(CameraPos, CameraTarget, Dir);
            Dir.normalise();
            Dir.scale(4);
            Dir.y += 0.1f;
            Vector3f.add(CameraPos, Dir, CameraPos);
            CameraPos.y += 1;

            // Little error correction: always make the camera above ground
            if(CameraPos.y < 0.01f)
                CameraPos.y = 0.01f;

            GLU.gluLookAt(CameraPos.x, CameraPos.y, CameraPos.z, CameraTarget.x, CameraTarget.y, CameraTarget.z, CameraUp.x, CameraUp.y, CameraUp.z);
        }
        // Overview
        else
        {
            GLU.gluLookAt(CDist * (float)Math.cos(Time), CDist, CDist * (float)Math.sin(Time), CameraPos.x, CameraPos.y, CameraPos.z, 0, 1, 0);
        }
        
        // Always face forward
        float Yaw = (float)Math.toDegrees(TestShip.GetYaw());
        
        // Render all elements
        TestWorld.Render(CameraPos, Yaw);
        TestShip.Render();
        
        // 2D GUI
        resizeGL2D();
        UI.Render(TestShip.GetRealVelocity(), TestShip.GetTargetVelocity(), TestShip.VEL_MAX);
    }
    
    public void update()
    {
        // Did the camera change?
        if(Keyboard.isKeyDown(Keyboard.KEY_Q))
            CameraType = !CameraType;
        
        TestShip.Update();
    }
}
