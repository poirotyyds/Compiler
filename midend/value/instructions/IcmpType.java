package midend.value.instructions;

public enum IcmpType {
    EQ,NE,SGT,SGE,SLT,SLE;

    @Override
    public String toString() {
        if (this.equals(IcmpType.EQ)) {
            return "eq";
        } else if (this.equals(IcmpType.NE)) {
            return "ne";
        } else if (this.equals(IcmpType.SGE)) {
            return "sge";
        } else if (this.equals(IcmpType.SGT)) {
            return "sgt";
        } else if (this.equals(IcmpType.SLE)) {
            return "sle";
        } else if (this.equals(IcmpType.SLT)) {
            return "slt";
        } else {
            return null;
        }
    }
}
