package JPA;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//      read the existing entries and write to console
        EntityManagerFactory emf= Persistence.createEntityManagerFactory("damPersistence");
        EntityManager em = emf.createEntityManager();



        List<Clase> clases=null;
        List<Instituto> institutos=null;
        boolean bucle=true;
        boolean bucle2;
        while(bucle){
            menu();
            int opcion=in.nextInt();
            in.nextLine();
            switch (opcion){
                case 0:
                    System.out.println("Adios");
                    bucle=false;
                    break;
                case 1:
                    Alumno alumno = new Alumno();
                    System.out.println("Introduce el DNI del alumno:");
                    alumno.setDni(in.nextInt());
                    in.nextLine();
                    System.out.println("Introduce el nombre del alumno:");
                    alumno.setNombre(in.nextLine());
                    System.out.println("Asgina una de las siguientes clases al alumno:");

                    clases = obtenClases(em);

                    for (Clase c: clases) {
                        System.out.print(c.getId()+", ");
                    }

                    int idClase = in.nextInt();
                    in.nextLine();

                    alumno.setClaseId(idClase);
                    em.getTransaction().begin();
                    em.persist(alumno);
                    em.getTransaction().commit();
                    sumaAlumno(em,idClase);
                    break;
                case 2:
                    Clase clase=new Clase();
                    System.out.println("Introduce el nombre de la clase:");
                    clase.setNombre(in.nextLine());
                    System.out.println("Introduce la rama de la clase:");
                    clase.setRama(in.nextLine());
                    //num alumnos 0;
                    clase.setNAlumnos(0);
                    System.out.println("Asgina la clase a uno de los siguientes institutos:");
                    TypedQuery<Instituto> query2 = em.createQuery("SELECT c FROM Instituto c",Instituto.class);
                    institutos = obtenInstitutos(em);
                    for (Instituto i: institutos) {
                        System.out.print(i.getId()+", ");
                    }
                    clase.setInstitutoId(in.nextInt());
                    in.nextLine();
                    System.out.println("Asigna a la clase un id:");
                    //actualizo clases
                    clases = obtenClases(em);
                    if (institutos!=null){
                        bucle2=true;
                    }else bucle2=false;
                    int id=in.nextInt();
                    while (bucle2){
                        for (Clase c: clases) {
                            if (c.getId() != id){
                                bucle2=false;
                            }else{
                                System.out.println("ID no disponible, introduce otro");
                                id=in.nextInt();
                            }
                        }
                    }
                    clase.setId(id);

                    em.getTransaction().begin();
                    em.persist(clase);
                    em.getTransaction().commit();
                    break;
                case 3:
                    Instituto instituto=new Instituto();
                    System.out.println("Introduce del Instituto:");
                    instituto.setNombre(in.nextLine());
                    System.out.println("Introduce un ID para el instituto:");
                    //obtengo institutos
                    institutos = obtenInstitutos(em);
                    id=0;
                    id=in.nextInt();
                    if (institutos!=null){
                        bucle2=true;
                    }else bucle2=false;
                    while (bucle2){
                        for (Instituto i: institutos) {
                            if (i.getId() != id){
                                bucle2=false;
                            }else{
                                System.out.println("ID no disponible, introduce otro");
                                id=in.nextInt();
                            }
                        }
                    }
                    instituto.setId(id);
                    instituto.setNAlumnos(0);

                    em.getTransaction().begin();
                    em.persist(instituto);
                    em.getTransaction().commit();
                    break;
                case 4:
                    //contar alumnos
                    System.out.println("Instituto | nAlumnos | count");
                    institutos=obtenInstitutos(em);
                    int nAlumnos=0;
                    for (Instituto i: institutos) {
                        nAlumnos = getNAlumnosInstituto(em,i.getId());
                        System.out.println(i.getNombre()+" | "+i.getNAlumnos()+" | "+nAlumnos);
                    }
                    break;
            }
        }
        em.getTransaction();

    }

    private static int getNAlumnosInstituto(EntityManager em, int idInstituto) {
        TypedQuery<Clase> query = em.createQuery("SELECT c FROM Clase c",Clase.class);
        List<Clase> clases = query.getResultList();
        int count=0;
        for (Clase c:clases) {
            if (c.getInstitutoId()==idInstituto){
                count+=c.getNAlumnos();
            }
        }
        return count;
    }

    private static void sumaAlumno(EntityManager em, int idClase) {
        em.getTransaction().begin();
        Clase clase = em.find(Clase.class,idClase);
        clase.setNAlumnos(clase.getNAlumnos()+1);
        Instituto instituto = em.find(Instituto.class,clase.getInstitutoId());
        instituto.setNAlumnos(instituto.getNAlumnos()+1);

        em.getTransaction().commit();
    }

    private static List<Instituto> obtenInstitutos(EntityManager em) {
        TypedQuery<Instituto> query = em.createQuery("SELECT c FROM Instituto c",Instituto.class);
        List<Instituto> institutos = query.getResultList();
        if (institutos==null)return null;
        else return institutos;
    }

    private static List<Clase> obtenClases(EntityManager em) {
        TypedQuery<Clase> query = em.createQuery("SELECT c FROM Clase c",Clase.class);
        List<Clase> clases = query.getResultList();
        return clases;
    }


    private static void menu() {
        System.out.println("1. Añadir Alumno\n" +
                "2. Añadir Clase\n" +
                "3. Añadir Instituto\n" +
                "4. Comprobar número alumnos\n" +
                "0. Salir");
    }
}
