public class Test {
    public static void main(String[] args) {
        String currentUrl = "http://search.sina.com.cn/?q=%C1%BD%BB%E1&c=news&from=index&col=&range=&source=&country=&size=&time=&a=&page=1&pf=2131294377&ps=2134309112&dpc=1";
        System.out.println("当前url"+currentUrl);
        int index = currentUrl.indexOf("&page=");
        System.out.println(index);
        int number = index+6;
        int thisPage = Character.getNumericValue(currentUrl.charAt(number));
        System.out.println("当前页码"+thisPage);
        int nextPage = thisPage+1;
        System.out.println("下一页"+nextPage);
    }
}
