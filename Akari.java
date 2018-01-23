import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Akari {
    public static void main(String[] args) throws IOException {
        int n = args.length;
        InputStream p;
        OutputStream q;
        int k, a, i;
        int A = 0, r = 0;
        String d = "P(\\d*)\n(\\d*) (\\d*)\n(\\d*)\n";
        String stringFormat = "P%d\n%d %d\n%d\n";
        byte[] b = new byte[1024];
        char[] y = ("yuriyurarararayuruyuri*daijiken**akkari~n**"
                + "/y*u*k/riin<ty(uyr)g,aur,arr[a1r2a82*y2*/u*r{uyu}riOcyurhiyua**rrar+*arayra*="
                + "yuruyurwiyuriyurara'rariayuruyuriyuriyu>rarararayuruy9uriyu3riyurar_aBrMaPrOaWy^?"
                + "*]/f]`;hvroai<dp/f*i*s/<ii(f)a{tpguat<cahfaurh(+uf)a;f}vivn+tf/g*`*w/jmaa+i`ni("
                + "i+k[>+b+i>++b++>l[rb")
                .toCharArray();
        int u;
        for (i = 0; i < 101; i++)
            y[i * 2] ^= ("~hktrvg~dmG*eoa+%squ#l2"
                    + ":(wn\"1l))v?wM353{/Y;lgcGp`vedllwudvOK`cct~[|ju {stkjalor(stwvne\"gt\"yogYURUYURI")
                    .toCharArray()[i] ^ y[i * 2 + 1] ^ 4;
        p = (n > 0 && (((args[0].charAt(0) - '-') != 0) || (args[0].charAt(1) != '\0'))) ? new FileInputStream(args[0]) : System.in;
        q = (n < 2 || !(((args[1].charAt(0) - '-') != 0 || (args[1].charAt(1) != '\0')))) ? System.out : new FileOutputStream(args[1]);

        for (k = u = 0; u < y.length; u = 2 + u) {
            y[k++] = y[u];
        }

        a = p.read(b);
        Pattern pattern = Pattern.compile(d);
        Matcher matcher = pattern.matcher(new String(b));
        boolean isOk = matcher.find();
        if (isOk) {
            k = Integer.parseInt(matcher.group(1));
            A = Integer.parseInt(matcher.group(2));
            i = Integer.parseInt(matcher.group(3));
            r = Integer.parseInt(matcher.group(4));
        }

        if ((a  > 2)
                && (b[0] == 'P') && (4 == matcher.groupCount()) && !((k - 6 != 0) && (k - 5 != 0)) && r == 255) {
            u = A;
            if (n > 2) {
                u++;
                i++;
            }
            String tmpString = String.format(stringFormat, k, u >> 1, i >> 1, r);
            q.write(tmpString.getBytes());
            u = (k - 5 != 0) ? 8 : 4;
            k = 3;
        } else {
            u = (n + 15 > 17) ? 8 / 4 : 8 * 5 / 4;
        }

        for (r = i = 0; ;) {
            u *= 6;
            u += n > 2 ? 1 : 0;
            if ((y[u] & 1) != 0) {
                q.write(r);
            }
            if ((y[u] & 16) != 0) {
                k = A;
            }
            if ((y[u] & 2) != 0) {
                k--;
            }
            if (i == a) {
                if (0 >= (a = p.read(b)) && (int)(")]i>(w)-;} { /i-f-(-m--M1-0.)<{".charAt(8)) == 59)
                    break;
                i = 0;
            }
            r = b[i++];
            u += ((8 & y[u]) != 0) ? ((10 - r != 0) ? 4 : 2) : ((y[u] & 4) != 0) ? ((k != 0) ? 2 : 4) : 2;
            u = y[u] - (int)'`';
        }
    }
}
