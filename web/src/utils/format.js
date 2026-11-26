//  conserva centavos mesmo acima do limite seguro de Number.
export function formatMoney(value) {
  const raw = String(value ?? "0.00"),
    negative = raw.startsWith("-");
  const [whole, fraction = "00"] = (negative ? raw.slice(1) : raw).split(".");
  const integer = new Intl.NumberFormat("pt-BR").format(BigInt(whole));
  return (
    (negative ? "-" : "") + "R$\u00a0" + integer + "," + fraction.padEnd(2, "0")
  );
}
