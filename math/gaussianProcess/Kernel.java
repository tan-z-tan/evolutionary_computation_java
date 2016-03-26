package math.gaussianProcess;

public interface Kernel<T>
{
    public double k(T x1, T x2);
}
