package name.abuchen.portfolio.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class CurrencyUnit implements Comparable<CurrencyUnit>
{
    private static final String BUNDLE_NAME = "name.abuchen.portfolio.util.currencies"; //$NON-NLS-1$
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    private static Map<String, CurrencyUnit> CACHE = new HashMap<String, CurrencyUnit>();

    static
    {
        Enumeration<String> codes = BUNDLE.getKeys();
        while (codes.hasMoreElements())
        {
            String currencyCode = codes.nextElement();
            String displayName = BUNDLE.getString(currencyCode);

            // currency symbol
            String currencySymbol = null;
            try
            {
                currencySymbol = BUNDLE.getString(currencyCode + ".symbol"); //$NON-NLS-1$
            }
            catch (MissingResourceException ignore)
            {
                // no symbol defined
            }

            CACHE.put(currencyCode, new CurrencyUnit(currencyCode, displayName, currencySymbol));
        }
    }

    private String currencyCode;
    private String displayName;
    private String currencySymbol;

    public static List<CurrencyUnit> getAvailableCurrencyUnits()
    {
        return new ArrayList<CurrencyUnit>(CACHE.values());
    }

    public static CurrencyUnit getInstance(String currencyCode)
    {
        return CACHE.get(currencyCode);
    }

    private CurrencyUnit(String currencyCode, String displayName, String currencySymbol)
    {
        this.currencyCode = currencyCode;
        this.displayName = displayName;
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    @Override
    public int compareTo(CurrencyUnit other)
    {
        return getCurrencyCode().compareTo(other.getCurrencyCode());
    }
}
