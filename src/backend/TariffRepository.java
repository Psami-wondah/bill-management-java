package backend;

public class TariffRepository extends FileRepository<Tariff> {

    public TariffRepository() {
        super("data/tariffs.dat");
    }

    @Override
    public String getId(Tariff tariff) {
        return tariff.getId();
    }

}
