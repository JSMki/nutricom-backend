package com.example.nutricom.models;

public class Food {
    private int idAlimento;
    private String nombreAlimento;
    private String descripcion;
    private String unidadMedida;
    private double caloriasPorPorcion;
    private double proteinas;
    private double carbohidratos;
    private double grasas;
    private String estado;

    public Food(int idAlimento, String nombreAlimento, String descripcion, String unidadMedida,
                double caloriasPorPorcion, double proteinas, double carbohidratos,
                double grasas, String estado) {
        this.idAlimento = idAlimento;
        this.nombreAlimento = nombreAlimento;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.caloriasPorPorcion = caloriasPorPorcion;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.grasas = grasas;
        this.estado = estado;
    }

    public int getIdAlimento() { return idAlimento; }
    public String getNombreAlimento() { return nombreAlimento; }
    public String getDescripcion() { return descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public double getCaloriasPorPorcion() { return caloriasPorPorcion; }
    public double getProteinas() { return proteinas; }
    public double getCarbohidratos() { return carbohidratos; }
    public double getGrasas() { return grasas; }
    public String getEstado() { return estado; }

    public void setNombreAlimento(String nombreAlimento) { this.nombreAlimento = nombreAlimento; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public void setCaloriasPorPorcion(double caloriasPorPorcion) { this.caloriasPorPorcion = caloriasPorPorcion; }
    public void setProteinas(double proteinas) { this.proteinas = proteinas; }
    public void setCarbohidratos(double carbohidratos) { this.carbohidratos = carbohidratos; }
    public void setGrasas(double grasas) { this.grasas = grasas; }
    public void setEstado(String estado) { this.estado = estado; }
}