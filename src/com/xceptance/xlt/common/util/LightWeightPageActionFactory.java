package com.xceptance.xlt.common.util;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.actions.Downloader;
import com.xceptance.xlt.common.actions.LightWeightPageAction;
import com.xceptance.xlt.common.actions.XhrLightWeightPageAction;
import com.xceptance.xlt.engine.XltWebClient;

public class LightWeightPageActionFactory extends URLActionDataExecutableFactory
{
    private XltProperties properties;

    private LightWeightPageAction previousAction;

    public LightWeightPageActionFactory(final XltProperties properties)
    {
        super();
        setProperties(properties);
        XltLogger.runTimeLogger.debug("Creating new Instance");
    }

    private void setProperties(final XltProperties properties)
    {
        ParameterUtils.isNotNull(properties, "XltProperties");
        this.properties = properties;
    }

    @Override
    public URLActionDataExecutable createPageAction(final String name,
                                                    final WebRequest request)
    {
        LightWeightPageAction action;
        if (this.previousAction == null)
        {
            action = new LightWeightPageAction(name, request);
            previousAction = action;
            action.setDownloader(createDownloader());
        }
        else
        {
            action = new LightWeightPageAction(previousAction, name, request,
                                               createDownloader());
        }
        this.previousAction = action;
        return action;
    }

    private Downloader createDownloader()
    {
        final Boolean userAgentUID = properties.getProperty("userAgent.UID", false);
        final int threadCount = properties.getProperty("com.xceptance.xlt.staticContent.downloadThreads",
                                                       1);

        final Downloader downloader = new Downloader(
                                                     (XltWebClient) previousAction.getWebClient(),
                                                     threadCount, userAgentUID);

        return downloader;

    }

    @Override
    public URLActionDataExecutable createXhrPageAction(final String name,
                                                       final WebRequest request)
    {
        if (previousAction == null)
        {
            throw new IllegalArgumentException("Xhr action cannot be the first action");
        }
        final XhrLightWeightPageAction xhrAction = new XhrLightWeightPageAction(
                                                                                previousAction,
                                                                                name,
                                                                                request,
                                                                                createDownloader());

        previousAction = xhrAction;
        return xhrAction;
    }
}
