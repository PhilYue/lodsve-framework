package lodsve.core.io.support;

import lodsve.core.io.ZookeeperResource;
import lodsve.core.utils.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 支持读取zookeeper里的数据，其他类型还是由{@link PathMatchingResourcePatternResolver}处理.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 2018-1-3-0003 11:11
 */
public class LodsvePathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {
    public LodsvePathMatchingResourcePatternResolver() {
        super(new LodsveResourceLoader());
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (!StringUtils.startsWith(locationPattern, LodsveResourceLoader.URL_PREFIX)) {
            return super.getResources(locationPattern);
        }

        int prefixEnd = locationPattern.indexOf(":") + 1;
        if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
            // a file pattern
            return findZookeeperPathMatchingResources(locationPattern);
        } else {
            // a single resource with the given name
            return new Resource[]{getResourceLoader().getResource(locationPattern)};
        }
    }

    private Resource[] findZookeeperPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            if(!rootDirResource.exists()) {
                continue;
            }
            result.addAll(doFindPathMatchingZookeeperResources(rootDirResource, subPattern));
        }

        return result.toArray(new Resource[result.size()]);
    }

    private Collection<? extends Resource> doFindPathMatchingZookeeperResources(Resource rootDirResource, String subPattern) {
        if (!(rootDirResource instanceof ZookeeperResource)) {
            return Collections.emptySet();
        }

        Set<Resource> result = new HashSet<>(16);
        String fullPattern = org.springframework.util.StringUtils.replace(((ZookeeperResource) rootDirResource).getPath(), File.separator, "/");
        if (!subPattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + org.springframework.util.StringUtils.replace(subPattern, File.separator, "/");

        doRetrieveMatchingResources(fullPattern, rootDirResource, result);
        return result;
    }

    private void doRetrieveMatchingResources(String fullPattern, Resource content, Set<Resource> result) {
        ZookeeperResource resource = (ZookeeperResource) content;
        List<String> children = resource.listChildren();
        for (String child : children) {
            String childPath = resource.getPath() + "/" + child;
            ZookeeperResource childResource = new ZookeeperResource(childPath);
            String currPath = org.springframework.util.StringUtils.replace(childPath, File.separator, "/");

            if (childResource.isFolder() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
                doRetrieveMatchingResources(fullPattern, childResource, result);
            }

            if (getPathMatcher().match(fullPattern, currPath)) {
                result.add(childResource);
            }
        }
    }
}