package kb.service.api.array;

@SuppressWarnings("unused")
public interface FormulaProvider {

    Reference[] getReferences(String formula);

    void applyFormula(String formula, TableArray data);
}
