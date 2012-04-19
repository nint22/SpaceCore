package spacecore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import java.util.ArrayList;
import java.util.Random;

// Star point
class StarPoint
{
    Vector3f Pt = new Vector3f();
    Vector3f Color = new Vector3f();
    float Scale;
}

// Models to render
class Models
{
    Vector3f Pt = new Vector3f();
    Model model;
    float Yaw;
}

/**
 * @author jbridon
 * A very simple world that is always rendered at Y = 0
 */
public class World
{
    // Box size
    public static float SkyboxSize = 32.0f;
    public static float WorldSize = 1024.0f;
    
    // List of stars (list of Vector3f)
    ArrayList<StarPoint> StarList;
    
    // Load a bunch of objects
    ArrayList<Models> ModelList;
    
    public World()
    {
        // Create a list and fill with a bunch of stars
        StarList = new ArrayList();
        for(int i = 0; i < 1000; i++)
        {
            // New star
            StarPoint Star = new StarPoint();
            
            // New position
            double u = 2f * Math.random() - 1f;
            double v = Math.random() * 2 * Math.PI;
            
            Star.Pt.x = (float)(Math.sqrt(1f - Math.pow(u, 2.0)) * Math.cos(v));
            Star.Pt.z = (float)(Math.sqrt(1f - Math.pow(u, 2.0)) * Math.sin(v));
            Star.Pt.y = (float)Math.abs(u);
            Star.Pt.scale(SkyboxSize / 2); // Scale out from the center
            
            // Scale up
            Star.Scale = 3f * (float)Math.random();
            
            // Color
            float Gray = 0.5f + 0.5f * (float)Math.random();
            Star.Color.x = Gray;
            Star.Color.y = Gray;
            Star.Color.z = Gray;
            
            // Push star into list
            StarList.add(Star);
        }
        
        // Load a bunch of models
        ModelList = new ArrayList();
        
        // Load road strip
        Models model = new Models();
        model.model = OBJLoader.load("src/Road.obj");
        model.Yaw = 0f;
        ModelList.add(model);
        
        // Load a bunch of rocks..
        for(int i = 0; i < 100; i++)
        {
            int Index = (int)(Math.random() * 5f) + 1;
            
            Models newModel = new Models();
            newModel.model = OBJLoader.load("src/Rock" + Index + ".obj");
            newModel.Yaw = (float)(Math.random() * 2.0 * Math.PI);
            
            newModel.Pt.x = (float)(Math.random() * 2.0 - 1.0) * SkyboxSize;
            newModel.Pt.z = (float)(Math.random() * 2.0 - 1.0) * SkyboxSize;
            newModel.Pt.y = 0f;
            
            ModelList.add(newModel);
        }
    }
    
    // Render the ship
    public void Render(Vector3f Pos, float Yaw)
    {
        // Rotate (yaw) as needed so the player always faces non-corners
        GL11.glPushMatrix();
        
            // Rotate and translate
            GL11.glTranslatef(Pos.x, Pos.y, Pos.z);
            GL11.glRotatef(Yaw, 0f, 1f, 0f);
            
            // Render the skybox and stars
            RenderSkybox();
        
        // Be done
        GL11.glPopMatrix();
        
        // Render out the stars
        GL11.glPushMatrix();
        
            // Show stars
            GL11.glTranslatef(Pos.x, Pos.y * 0.99f, Pos.z);
            RenderStars();
        
        // Be done
        GL11.glPopMatrix();
        
        // Draw stars
        GL11.glPushMatrix();
        
            // Render ground and right below
            GL11.glTranslatef(Pos.x, 0, Pos.z);
            
            Vector3f Color = new Vector3f(236.0f / 255.0f, 200.0f / 255.0f, 122.0f / 255.0f);
            RenderGround(WorldSize, Color);
            
        GL11.glPopMatrix();
        
        // Render all the objects
        for(Models model : ModelList)
        {
            GL11.glPushMatrix();
            
                // Render ground and right below
                GL11.glTranslatef(model.Pt.x, model.Pt.y, model.Pt.z);
                GL11.glRotatef((float)Math.toDegrees(model.Yaw), 0, 1, 0);
                RenderModel(model.model);
            
            GL11.glPopMatrix();
        }
    }
    
    // Draw the bottom level
    public void RenderGround(float WorldLength, Vector3f Color)
    {
        // Translate to position
        GL11.glPushMatrix();
        
        // Set the ship color to red for now
        GL11.glColor3f(Color.x, Color.y, Color.z);
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(-WorldLength, 0, -WorldLength);
            GL11.glVertex3f(WorldLength, 0, -WorldLength);
            GL11.glVertex3f(WorldLength, 0, WorldLength);
            GL11.glVertex3f(-WorldLength, 0, WorldLength);
        GL11.glEnd();
        
        // Done
        GL11.glPopMatrix();
    }
    
    // Render the 
    public void RenderSkybox()
    {
        // Define the top and bottom color
        Vector3f TopColor = new Vector3f(204f / 255f, 255f / 255f, 255f / 255f);
        Vector3f BottomColor = new Vector3f(207f / 255f, 179f / 255f, 52f / 255f);
        
	// Save matrix
	glPushMatrix();

            // Draw out top side
            glBegin(GL_QUADS);

            // Polygon & texture map
            // Top has one constant color
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(-SkyboxSize, SkyboxSize, -SkyboxSize);
            glVertex3f(SkyboxSize, SkyboxSize, -SkyboxSize);
            glVertex3f(SkyboxSize, SkyboxSize, SkyboxSize);
            glVertex3f(-SkyboxSize, SkyboxSize, SkyboxSize);

            glEnd();

            // Drow out the left side
            glBegin(GL_QUADS);

            // Polygon & texture map
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(SkyboxSize, SkyboxSize, -SkyboxSize);
            glColor3f(BottomColor.x, BottomColor.y, BottomColor.z);
            glVertex3f(SkyboxSize, -SkyboxSize, -SkyboxSize);
            glVertex3f(SkyboxSize, -SkyboxSize, SkyboxSize);
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(SkyboxSize, SkyboxSize, SkyboxSize);

            glEnd();

            // Drow out the right side
            glBegin(GL_QUADS);

            // Polygon & texture map
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(-SkyboxSize, SkyboxSize, SkyboxSize);
            glColor3f(BottomColor.x, BottomColor.y, BottomColor.z);
            glVertex3f(-SkyboxSize, -SkyboxSize, SkyboxSize);
            glVertex3f(-SkyboxSize, -SkyboxSize, -SkyboxSize);
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(-SkyboxSize, SkyboxSize, -SkyboxSize);

            glEnd();

            // Drow out the front side
            glBegin(GL_QUADS);

            // Polygon & texture map
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(SkyboxSize, SkyboxSize, SkyboxSize);
            glColor3f(BottomColor.x, BottomColor.y, BottomColor.z);
            glVertex3f(SkyboxSize, -SkyboxSize, SkyboxSize);
            glVertex3f(-SkyboxSize, -SkyboxSize, SkyboxSize);
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(-SkyboxSize, SkyboxSize, SkyboxSize);

            glEnd();

            // Drow out the back side
            glBegin(GL_QUADS);

            // Polygon & texture map
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(-SkyboxSize, SkyboxSize, -SkyboxSize);
            glColor3f(BottomColor.x, BottomColor.y, BottomColor.z);
            glVertex3f(-SkyboxSize, -SkyboxSize, -SkyboxSize);
            glVertex3f(SkyboxSize, -SkyboxSize, -SkyboxSize);
            glColor3f(TopColor.x, TopColor.y, TopColor.z);
            glVertex3f(SkyboxSize, SkyboxSize, -SkyboxSize);

            glEnd();
	
	// Place back matrix
	glPopMatrix();
    }
    
    // Render the stars
    public void RenderStars()
    {
        // Render all stars
        for(StarPoint Star : StarList)
        {
            glPointSize(Star.Scale);
            glColor3f(Star.Color.x, Star.Color.y, Star.Color.z);
            glBegin(GL_POINTS);
                glVertex3f(Star.Pt.x, Star.Pt.y, Star.Pt.z);
            glEnd();
        }
    }
    
    // Render a model or shape
    public void RenderModel(Model model)
    {
        // Set width to a single line
        GL11.glLineWidth(1);
        
        // Change rendermode
        for(int i = 0; i < 2; i++)
        {
            if(i == 0)
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            else
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            
            // Randomize surface color a bit
            Random SurfaceRand = new Random(123456);
            
            GL11.glBegin(GL11.GL_TRIANGLES);
            for (Face face : model.faces)
            {
                // Always make black when in line mode)
                if(i == 0)
                    GL11.glColor3f(0.8f, 0.8f, 0.5f + 0.5f * (SurfaceRand.nextFloat()));
                else
                    GL11.glColor3f(0.4f, 0.4f, 0.2f + 0.2f * (SurfaceRand.nextFloat()));
                
                // Randomize the color a tiny bit
                Vector3f v1 = model.vertices.get((int) face.vertex.x - 1);
                GL11.glVertex3f(v1.x, v1.y, v1.z);
                Vector3f v2 = model.vertices.get((int) face.vertex.y - 1);
                GL11.glVertex3f(v2.x, v2.y, v2.z);
                Vector3f v3 = model.vertices.get((int) face.vertex.z - 1);
                GL11.glVertex3f(v3.x, v3.y, v3.z);
            }
            GL11.glEnd();
        }
    }
}
