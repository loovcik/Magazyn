package me.loovcik.magazyn.items;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Storage
{
	/**
	 * Przechowuje ilości przedmiotów
	 */
	private final Map<Material, ItemData> container;

	/**
	 * Określa, czy gracz ma włączone automatyczne
	 * przerzucanie itemów do magazynu
	 */
	private boolean enabled;

	/**
	 * Określa, czy dane zostały zmienione
	 */
	private boolean changed;

	/** Określa, czy powiadomienia o depozycie mają być wyświetlane */
	private boolean notification;

	/**
	 * Pobiera informację, czy gracz ma włączoną funkcję<br>
	 * automatycznego pobierania przedmiotów do magazynu
	 */
	public boolean isEnabled() { return enabled; }

	/**
	 * Ustawia wartość, określającą, czy gracz ma włączoną<br>
	 * funkcję automatycznego pobierania przedmiotów<br>
	 * do magazynu
	 */
	public void setEnabled(boolean value) {
		boolean oldValue = enabled;
		enabled = value;
		if (enabled != oldValue)
			setChanged();
	}

	/** Określa, czy powiadomienia o depozycie mają być wyświetlane */
	public boolean hasNotification() { return notification; }

	/** Ustawia wartość określającą, czy powiadomienia o depozycie mają być pokazywane */
	public void setNotification(boolean value) {
		notification = value;
		setChanged();
	}

	/**
	 * Pobiera informację, czy dane<br>
	 * zostały zmienione
	 */
	public boolean isChanged() { return changed; }

	/**
	 * Oznacza flagę zmienionych danych
	 */
	public void setChanged() { setChanged(true); }

	/**
	 * Zmienia flagę, określającą, czy dane zostały zmienione
	 */
	public void setChanged(boolean value) { changed = value; }

	/**
	 * Sprawdza, czy gracz ma dane o przedmiocie
	 */
	public boolean isExists(Material material){ return container.containsKey(material);	}

	/**
	 * Dodaje określoną ilość przedmiotów do magazynu gracza
	 * @param material Rodzaj przedmiotu
	 * @param amount Ilość do dodania
	 */
	public void add(Material material, int amount){
		if (material == null || amount == 0) return;
		if (!isExists(material)) createItemData(material, amount, true);
		else {
			ItemData value = container.get(material);
			value.amount += amount;
			container.replace(material, value);
		}
		changed = true;
	}

	/**
	 * Usuwa określoną ilość przedmiotów z magazynu gracza
	 * @param material Rodzaj przedmiotu
	 * @param amount Ilość do usunięcia
	 */
	public void remove(Material material, int amount){
		if (!isExists(material)) return;
		ItemData value = container.get(material);
		value.amount -= amount;
		value.amount = Integer.max(0, value.amount);
		container.replace(material, value);
		changed = true;
	}

	/**
	 * Pobiera dane przedmiotu z magazynu gracza
	 * @param material Rodzaj przedmiotu
	 */
	public ItemData get(Material material){
		if (!isExists(material)) createItemData(material, 0, true);
		return container.get(material);
	}

	/**
	 * Pobiera ilość przedmiotów danego typu,<br>
	 * która znajduje się w magazynie gracza
	 */
	public int getAmount(Material material){
		if (material == null || !container.containsKey(material)) return 0;
		return container.get(material).amount;
	}

	/**
	 * Podaje całkowitą ilość przedmiotów zgromadzonych<br>
	 * w magazynie gracza
	 */
	public int getAmount(){
		int result = 0;
		for (ItemData data : container.values())
			result += data.amount;
		return result;
	}

	/**
	 * Ustawia ilość przedmiotów danego typu w magazynie<br>
	 * gracza na określoną wartość
	 */
	public void setAmount(Material material, int amount){
		if (material == null) return;
		amount = Integer.max(0, amount);
		if (!isExists(material)) createItemData(material, amount, true);
		else {
			container.get(material).amount = amount;
		}
		changed = true;
	}

	/**
	 * Pobiera informację, czy dany typ przedmiotów<br>
	 * jest włączony w magazynie
	 */
	public boolean isEnabled(Material material) {
		if (material == null) return false;
		if (!isExists(material)) container.put(material, new ItemData(0, true));
		return get(material).enabled;
	}

	/**
	 * Ustawia wartość, określającą, czy dany rodzaj przedmiotów<br>
	 * jest włączony w magazynie
	 */
	public void setEnabled(Material material, boolean value){
		if (material == null) return;
		if (!isExists(material)) container.put(material, new ItemData(0, value));
		else get(material).enabled = value;
	}

	public void createItemData(Material material){
		createItemData(material, 0, true);
	}
	/**
	 * Tworzy dane przedmiotu w magazynie gracza
	 */
	@SuppressWarnings("SameParameterValue")
	private void createItemData(Material material, int amount, boolean enabled){
		container.put(material, new ItemData(amount, enabled));
	}

	public Storage(){
		container = new HashMap<>();
		enabled = true;
	}
}