package kb.service.api.array;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public interface Reference {

    boolean contains(int index);

    int getSize();

    int getIndex(int i);

    static Reference arrayReference(Reference... references) {
        Set<Integer> set = new HashSet<>();
        for (Reference reference : references) {
            for (int i = 0; i < reference.getSize(); i++) {
                set.add(reference.getIndex(i));
            }
        }
        int[] arr = new int[set.size()];
        int i = 0;
        for (int ref : set) {
            arr[i++] = ref;
        }
        return new MultiCellReference(arr);
    }

    static boolean referenceEquals(Reference a, Reference b) {
        if (a == null || b == null || a.getSize() != b.getSize()) {
            return false;
        }
        for (int i = 0; i < a.getSize(); i++) {
            if (a.getIndex(i) != b.getIndex(i)) {
                return false;
            }
        }
        return true;
    }
}
