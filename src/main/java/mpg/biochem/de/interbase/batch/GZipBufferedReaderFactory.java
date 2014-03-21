package mpg.biochem.de.interbase.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

/**
 * GZipBufferedReaderFactory provides Resourcehandling of gzip Files and still
 * works with normal flat files.
 * 
 * @author Michael R. Lange <michael.r.lange@langmi.de>
 * @see <a href="http://php.sabscape.com/blog/?p=281">sabscape.com: Customizing Spring Batch to process zipped files</a>
 */
public class GZipBufferedReaderFactory implements BufferedReaderFactory {

    /** Default value for gzip suffixes. */
    private List<String> gzipSuffixes = new ArrayList() {
        {
            add(".gz");
            add(".gzip");
        }
    };

    /**
     * Creates Bufferedreader for gzip Resource, handles normal resources
     * too.
     * 
     * @param resource
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    @Override
    public BufferedReader create(Resource resource, String encoding)
            throws UnsupportedEncodingException, IOException {
        for (String suffix : gzipSuffixes) {
            // test for filename and description, description is used when 
            // handling itemStreamResources
            if (resource.getFilename().endsWith(suffix)
                    || resource.getDescription().endsWith(suffix)) {
                return new BufferedReader(new InputStreamReader(new GZIPInputStream(resource.getInputStream()), encoding));
            }
        }
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding));
    }

    public List<String> getGzipSuffixes() {
        return gzipSuffixes;
    }

    public void setGzipSuffixes(List<String> gzipSuffixes) {
        this.gzipSuffixes = gzipSuffixes;
    }
}