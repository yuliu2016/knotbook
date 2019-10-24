package kb.service.api.array;

public interface FormulaProvider {

    Reference[] getReferences(String formula);

    void applyFormula(String formula, TableArray data);
}
