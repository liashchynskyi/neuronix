package neuronix.models.json;

public class Layer {
    
    private Integer id;
    private String  type, name, activation, loss;
    private Integer out, bias;
    private int[] kernel, stride, padding;
    
    
    public String getLoss () {
        return loss;
    }
    
    public void setLoss (String loss) {
        this.loss = loss;
    }
    
    public Integer getId () {
        return id;
    }
    
    public void setId (Integer id) {
        this.id = id;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    public String getActivation () {
        return activation;
    }
    
    public void setActivation (String activation) {
        this.activation = activation;
    }
    
    public Integer getOut () {
        return out;
    }
    
    public void setOut (Integer out) {
        this.out = out;
    }
    
    public Integer getBias () {
        return bias;
    }
    
    public void setBias (Integer bias) {
        this.bias = bias;
    }
    
    public int[] getKernel () {
        return kernel;
    }
    
    public void setKernel (int[] kernel) {
        this.kernel = kernel;
    }
    
    public int[] getStride () {
        return stride;
    }
    
    public void setStride (int[] stride) {
        this.stride = stride;
    }
    
    public int[] getPadding () {
        return padding;
    }
    
    public void setPadding (int[] padding) {
        this.padding = padding;
    }
}