package control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Manejo_Archivos 
{

    //Lee un archivo CSV y devuelve una lista donde cada elemento es un arreglo de Strings (las columnas).
    //@ Ruta del archivo (ej. "data/inventario.csv")
    public static List<String[]> leerCSV(String rutaArchivo) 
    {
        List<String[]> registros = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) 
        {
            String linea;
            while ((linea = br.readLine()) != null) 
            {
                // Separamos los valores usando la coma como delimitador
                String[] valores = linea.split(",");
                registros.add(valores);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        
        return registros;
    }

    public static List<String[]> leerDatosCSV(String rutaArchivo, String primeraColumnaEncabezado) 
    {
        List<String[]> registros = leerCSV(rutaArchivo);
        if (!registros.isEmpty() && registros.get(0).length > 0
                && registros.get(0)[0].trim().equalsIgnoreCase(primeraColumnaEncabezado)) 
        {
            registros.remove(0);
        }
        return registros;
    }

    //Agrega un nuevo registro al final del archivo CSV sin borrar lo anterior (Modo Append).
    //@param rutaArchivo Ruta del archivo
    //@param datosNuevos Línea de texto separada por comas (ej. "3,Mesa de Centro,2500,4500,10")
    public static void escribirCSV(String rutaArchivo, String datosNuevos) 
    {
        // El 'true' en el FileWriter indica que se agregará al final del archivo
        try (FileWriter fw = new FileWriter(rutaArchivo, true);
             PrintWriter pw = new PrintWriter(fw)) 
        {
            pw.println(datosNuevos);
        } 
        catch (IOException e) 
        {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    //Sobrescribe todo el archivo CSV. Es muy útil cuando modificas el stock de un producto
    //o editas un precio, ya que reescribe la lista completa con los cambios actualizados.
    //@param rutaArchivo Ruta del archivo
    //@param listaCompleta Lista con todas las líneas de texto a guardar (incluyendo encabezados)
    public static void sobrescribirCSV(String rutaArchivo, List<String> listaCompleta) 
    {
        // Al omitir el 'true', FileWriter sobrescribe el archivo desde cero
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo, false))) 
        {
            for (String linea : listaCompleta) 
            {
                pw.println(linea);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error al actualizar el archivo: " + e.getMessage());
        }
    }
}