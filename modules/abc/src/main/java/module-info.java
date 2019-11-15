module kb.abc {
    requires kb.service.abc;
    exports kb.abc;

    uses kb.service.abc.ABC;
}