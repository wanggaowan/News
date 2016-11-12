package com.suixingame.news;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    List<PersonB> personBs;
    @Test
    public void addition_isCorrect () throws Exception {
        List<Person> persons = new ArrayList<> ();
        Person ww = new Person ("ww", 22);
        Person wc = new Person ("wc", 22);
        Person wd = new Person ("wd", 22);
        persons.add (ww);
        persons.add (wc);
        persons.add (wd);

        personBs = new ArrayList<> ();
        PersonB we = new PersonB ("we", 22);
        PersonB wf = new PersonB ("wf", 22);
        PersonB wg = new PersonB ("wg", 22);
        personBs.add (we);
        personBs.add (wf);
        personBs.add (wg);
        for (int i = 0; i <persons.size () ; i++) {
            Person person = persons.get (i);
            setMoney (person);

        }
        for(Person person:persons){
            System.out.println (person.getPersonBs ().size ());
        }
    }
    @Test
    public void testList(){
        List<Integer> list = new ArrayList<> ();
        list.add (5);
        list.add (6);
        list.add (7);
        list.add (8);
        list.add (9);

        List<Integer> list2 = new ArrayList<> ();
        list2.add (0);
        list2.add (1);
        list2.add (2);
        list2.add (3);
        list2.add (4);
        list.addAll (0,list2);
        for(int a:list){
            System.out.println (a);
        }
    }
    @Test
    public void testMd5(){
         Map<Integer,Boolean> mListViewIsInit = new HashMap<> ();
        mListViewIsInit.put (0,false);
        mListViewIsInit.remove (1);
    }


    private void setMoney(Person person){
        person.setPersonBs (personBs);
    }

    public class Person {
        public String name;
        public int age;
        private List<PersonB> mPersonBs;

        public Person (String name, int age) {
            this.name = name;
            this.age = age;
        }

        public List<PersonB> getPersonBs () {
            return mPersonBs;
        }

        public void setPersonBs (List<PersonB> personBs) {
            mPersonBs = personBs;
        }
    }

    public class PersonB {
        public String name;
        public int age;

        public PersonB (String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}