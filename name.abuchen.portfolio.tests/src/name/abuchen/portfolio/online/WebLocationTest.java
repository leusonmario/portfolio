package name.abuchen.portfolio.online;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;

import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.online.impl.YahooFinanceQuoteFeed;

import org.junit.Test;

@SuppressWarnings("nls")
public class WebLocationTest
{

    @Test
    public void testURLCreation() throws IOException, URISyntaxException
    {
        WebLocation page = new WebLocation("", "http://-{tickerSymbol}-{isin}-");
        Security security = new Security("Daimler", "DE0007100000", "DAI.DE", YahooFinanceQuoteFeed.ID);

        assertThat(page.constructURL(security).toString(), equalTo("http://-DAI.DE-DE0007100000-"));
    }
}
