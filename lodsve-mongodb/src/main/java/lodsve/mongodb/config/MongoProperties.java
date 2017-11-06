package lodsve.mongodb.config;

import lodsve.core.autoconfigure.annotations.ConfigurationProperties;

import java.util.Map;

/**
 * mongodb base properties.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/1/21 下午6:55
 */
@ConfigurationProperties(prefix = "lodsve.mongo", locations = "file:${params.root}/framework/mongo.properties")
public class MongoProperties {
    private int maxpoolsize;
    private Map<String, MongoConnection> project;

    public int getMaxpoolsize() {
        return maxpoolsize;
    }

    public void setMaxpoolsize(int maxpoolsize) {
        this.maxpoolsize = maxpoolsize;
    }

    public Map<String, MongoConnection> getProject() {
        return project;
    }

    public void setProject(Map<String, MongoConnection> project) {
        this.project = project;
    }

    public static class MongoConnection{
        private String url;
        private String username;
        private String password;
        private int maxpoolsize;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaxpoolsize() {
            return maxpoolsize;
        }

        public void setMaxpoolsize(int maxpoolsize) {
            this.maxpoolsize = maxpoolsize;
        }
    }
}
