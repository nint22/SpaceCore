package spacecore;

import java.io.*;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Oskar
 */
public class OBJLoader {
    public static Model loadModel(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        Model m = new Model();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("v ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.vertices.add(new Vector3f(x,y,z));
            } else if (line.startsWith("f ")) {
                try {
                    Vector3f vertexIndices = new Vector3f(
                            Float.valueOf(line.split(" ")[1].split("/")[0]), 
                            Float.valueOf(line.split(" ")[2].split("/")[0]),
                            Float.valueOf(line.split(" ")[3].split("/")[0]));
                    m.faces.add(new Face(vertexIndices));
                }
                catch(Exception e) { }
            }
        }
        reader.close();
        return m;
    }

    public static Model load(String FileString)
    {
        Model model = new Model();
        try {
            model = OBJLoader.loadModel(new File(FileString));
        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }
        return model;
    }
}