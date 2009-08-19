/*
 * Copyright © 2008-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.orfjackal.pommac.util;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.pommac.TestUtil;
import org.junit.runner.RunWith;

import java.io.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@RunWith(JDaveRunner.class)
public class WorkDirectoryManagerSpec extends Specification<Object> {

    private File parentDir;

    public void create() {
        parentDir = TestUtil.createWorkDir();
    }

    public void destroy() {
        TestUtil.deleteWorkDir();
    }


    public class CreatingAWorkDirectory {

        private WorkDirectoryManager manager;

        public void create() {
            manager = new WorkDirectoryManager(parentDir);
        }

        public void atFirstThereAreNoDirectories() {
            specify(parentDir.listFiles(), does.containExactly());
        }

        public void directoriesAreCreatedInTheTargetDirectory() {
            File dir = manager.newDirectory();
            specify(parentDir.listFiles(), does.containExactly(dir));
            specify(dir.isDirectory());
            specify(dir.listFiles(), does.containExactly());
        }

        public void eachNewDirectoryHasAUniqueName() {
            File dir1 = manager.newDirectory();
            File dir2 = manager.newDirectory();
            specify(dir1.isDirectory());
            specify(dir2.isDirectory());
            specify(dir1.getName(), does.not().equal(dir2.getName()));
            specify(parentDir.listFiles(), does.containExactly(dir1, dir2));
        }

        public void onDisposeAllCreatedDirectoriesAreDeleted() {
            File dir = manager.newDirectory();
            specify(dir.exists());
            manager.dispose();
            specify(!dir.exists());
            specify(parentDir.exists());
        }

        public void onDisposeAllFilesInCreatedDirectoriesAreDeleted() throws IOException {
            File dir = manager.newDirectory();
            new File(dir, "foo.txt").createNewFile();
            specify(dir.exists());
            specify(dir.list(), does.containExactly("foo.txt"));
            manager.dispose();
            specify(!dir.exists());
        }

        public void willNotDeleteFilesOutsideTheCreatedDirectories() throws IOException {
            File foo = new File(parentDir, "foo.txt");
            foo.createNewFile();
            File dir = manager.newDirectory();
            specify(foo.exists());
            specify(dir.exists());
            manager.dispose();
            specify(foo.exists());
            specify(!dir.exists());
        }
    }
}
