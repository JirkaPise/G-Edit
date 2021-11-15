package seznam;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrDoubleList<T> implements IAbstrDoubleList<T>, Serializable {

    private Prvek<T> prvni;
    private Prvek<T> posledni;
    private Prvek<T> aktualni;

    @Override
    public void zrus() {
        prvni = null;
        posledni = null;
        aktualni = null;
    }

    @Override
    public boolean jePrazdny() {
        return prvni == null;
    }

    @Override
    public void vlozPrvni(T data) {
        Prvek<T> novyPrvek = new Prvek<>(data, prvni, null);
        if (prvni != null) {
            prvni.predchozi = novyPrvek;
        } else {
            posledni = novyPrvek;
        }
        prvni = novyPrvek;
        aktualni = novyPrvek;

    }

    @Override
    public void vlozPosledni(T data) {
        Prvek<T> novyPrvek = new Prvek<>(data, null, posledni);
        if (prvni != null) {
            posledni.dalsi = novyPrvek;
        } else {
            prvni = novyPrvek;
        }
        posledni = novyPrvek;
        aktualni = novyPrvek;
    }

    @Override
    public void vlozNaslednika(T data) {
        prazdny();
        aktualniBylNastaven();
        Prvek<T> novyPrvek = new Prvek<>(data, aktualni.dalsi, aktualni);
        if (novyPrvek.dalsi == null) {
            posledni = novyPrvek;
        } else {
            aktualni.dalsi.predchozi = novyPrvek;
        }
        aktualni.dalsi = novyPrvek;
        aktualni = novyPrvek;

    }

    @Override
    public void vlozPredchudce(T data) {
        prazdny();
        aktualniBylNastaven();
        Prvek<T> novyPrvek = new Prvek<>(data, aktualni, aktualni.predchozi);
        if (novyPrvek.predchozi == null) {
            prvni = novyPrvek;
        }else{
            aktualni.predchozi.dalsi = novyPrvek;
        }
        aktualni.predchozi = novyPrvek;
        aktualni = novyPrvek;

    }

    @Override
    public T zpristupniAktualni() {
        prazdny();
        aktualniBylNastaven();
        return aktualni.data;
    }

    @Override
    public T zpristupniPrvni() {
        prazdny();
        aktualni = prvni;
        return aktualni.data;
    }

    @Override
    public T zpristupniPosledni() {
        prazdny();
        aktualni = posledni;
        return aktualni.data;
    }

    @Override
    public T zpristupniNaslednika() {
        prazdny();
        aktualniBylNastaven();
        if (aktualni == posledni) {
            throw new RuntimeException("Tento prvek nemá následníka!");
        }
        aktualni = aktualni.dalsi;
        return aktualni.data;
    }

    @Override
    public T zpristupniPredchudce() {
        prazdny();
        aktualniBylNastaven();
        if (aktualni == prvni) {
            throw new RuntimeException("Tento prvek nemá předchůdce!");
        }
        aktualni = aktualni.predchozi;
        return aktualni.data;
    }

    @Override
    public T odeberAktualni() {
        prazdny();
        aktualniBylNastaven();
        Prvek<T> odebiranyPrvek = aktualni;
        if (aktualni == posledni && aktualni == prvni) {
            aktualni = null;
            prvni = null;
            posledni = null;
        } else if (aktualni == posledni) {
            posledni = aktualni.predchozi;
            posledni.dalsi = null;
            aktualni = posledni;
        } else if (aktualni == prvni) {
            prvni = aktualni.dalsi;
            prvni.predchozi = null;
            aktualni = prvni;
        } else {
            aktualni.predchozi.dalsi = aktualni.dalsi;
            aktualni.dalsi.predchozi = aktualni.predchozi;
            aktualni = aktualni.predchozi;
        }
        return odebiranyPrvek.data;
    }

    @Override
    public T odeberPrvni() {
        prazdny();
        Prvek<T> odebiranyPrvek = prvni;
        if (prvni == posledni) {
            prvni = null;
            posledni = null;
            aktualni = null;
        } else {
            prvni = prvni.dalsi;
            prvni.predchozi = null;
            aktualni = prvni;
        }
        return odebiranyPrvek.data;
    }

    @Override
    public T odeberPosledni() {
        prazdny();
        Prvek<T> odebiranyPrvek = posledni;
        if (posledni == prvni) {
            posledni = null;
            prvni = null;
            aktualni = null;
        } else {
            posledni = posledni.predchozi;
            posledni.dalsi = null;
            aktualni = posledni;
        }
        return odebiranyPrvek.data;
    }

    @Override
    public T odeberNaslednika() {
        prazdny();
        aktualniBylNastaven();
        if (aktualni.dalsi == null) {
            throw new RuntimeException("Tento prvek nemá následníka!");
        }
        Prvek<T> odebiranyPrvek = aktualni.dalsi;
        if (aktualni.dalsi == posledni) {
            posledni = aktualni;
            posledni.dalsi = null;
        } else {
            aktualni.dalsi = aktualni.dalsi.dalsi;
            aktualni.dalsi.predchozi = aktualni;
        }
        return odebiranyPrvek.data;
    }

    @Override
    public T odeberPredchudce() {
        prazdny();
        aktualniBylNastaven();
        if (aktualni.predchozi == null) {
            throw new RuntimeException("Tento prvek nemá předchůdce!");
        }
        Prvek<T> odebiranyPrvek = aktualni.predchozi;
        if (aktualni.predchozi == prvni) {
            prvni = aktualni;
            prvni.predchozi = null;
        } else {
            aktualni.predchozi = aktualni.predchozi.predchozi;
            aktualni.predchozi.dalsi = aktualni;
        }

        return odebiranyPrvek.data;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorImpl();
    }

    private static class Prvek<T> implements Serializable{

        T data;
        Prvek<T> dalsi;
        Prvek<T> predchozi;

        public Prvek(T data, Prvek<T> dalsi, Prvek<T> predchozi) {
            this.data = data;
            this.dalsi = dalsi;
            this.predchozi = predchozi;
        }

    }

    private class IteratorImpl implements Iterator<T> {

        Prvek<T> prvek = prvni;

        @Override
        public boolean hasNext() {
            return prvek != null;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T data = prvek.data;
                prvek = prvek.dalsi;
                return data;
            } else {
                throw new NoSuchElementException();
            }
        }

    }

    private void aktualniBylNastaven() {
        if (aktualni == null) {
            throw new RuntimeException("Aktuální prvek nebyl nastaven!");
        }
    }

    private void prazdny() {
        if (prvni == null) {
            throw new RuntimeException("Seznam je prazdný!");
        }
    }

}
