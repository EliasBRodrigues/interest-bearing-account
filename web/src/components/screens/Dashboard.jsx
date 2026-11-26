import { useRef, useState, useCallback, useEffect } from "react";
import { Button } from "primereact/button";
import { Message } from "primereact/message";
import { Toast } from "primereact/toast";
import { Skeleton } from "primereact/skeleton";
import './styles/dash-style.css';

const date = (value) =>
  new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value));

  const types = {
  DEPOSITO: { label: "Depósito", icon: "pi-plus", severity: "info" },
  SAQUE: { label: "Saque", icon: "pi-minus", severity: "secondary" },
  JUROS: { label: "Rendimento", icon: "pi-percentage", severity: "success" },
};

export default function Dashboard({ session, onLogout, createdNumber }) {
  
  const [balance, setBalance] = useState(null),
    [statement, setStatement] = useState({ content: [], totalElements: 0 });
  
  const [page, setPage] = useState(0),
    [size, setSize] = useState(20),
    [loading, setLoading] = useState(true),
    [error, setError] = useState("");
  
  const [kind, setKind] = useState(null),
    [updated, setUpdated] = useState(null);
  
  const toast = useRef(null),
    requestId = useRef(0),
    controller = useRef(null);
  
  const refresh = useCallback(
    async (silent = false) => {
      const id = ++requestId.current;
      controller.current?.abort();
      controller.current = new AbortController();
      if (!silent) setLoading(true);
      try {
        const [nextBalance, nextStatement] = await Promise.all([
          api.balance(session, controller.current.signal),
          api.statement(session, page, size, controller.current.signal),
        ]);
        if (id !== requestId.current) return;
        setBalance(nextBalance);
        setStatement(nextStatement);
        setUpdated(new Date());
        setError("");
      } catch (e) {
        if (e.name !== "AbortError" && id === requestId.current)
          setError(e.message);
      } finally {
        if (id === requestId.current) setLoading(false);
      }
    },
    [session, page, size],
  );

  useEffect(() => {
    refresh();
    const timer = setInterval(() => {
      if (document.visibilityState === "visible") refresh(true);
    }, 30000);
    return () => {
      clearInterval(timer);
      requestId.current++;
      controller.current?.abort();
    };
  }, [refresh]);

  function success() {
    setKind(null);
    toast.current?.show({
      severity: "success",
      summary: "Movimentação concluída",
      detail: "O saldo e o extrato estão sendo atualizados.",
      life: 4500,
    });
    refresh();
  }

  return (
    <>
      <Toast ref={toast} />
      <header className="topbar">
        <div className="container header-inner">
          <a className="brand light" href="/">
            Conta Remunerada
          </a>
          <div className="header-right">
            <span className="secure-label">
              <i className="pi pi-lock" /> Área da sua conta
            </span>
            <Button
              label="Sair"
              icon="pi pi-sign-out"
              text
              onClick={onLogout}
              className="logout"
            />
          </div>
        </div>
      </header>
      <main className="container dashboard">
        <div className="page-heading">
          <div>
            <div className="eyebrow">MINHA CONTA</div>
            <h1>
              Olá, {balance?.titular || "bem-vindo"}
              <span className="heading-dot">.</span>
            </h1>
          </div>
          <Button
            label="Atualizar"
            icon="pi pi-refresh"
            outlined
            onClick={() => refresh()}
            loading={loading}
          />
        </div>
         {createdNumber && (
          <div className="welcome-message" role="status">
            <Message
              severity="success"
              text={
                "Sua conta foi criada! Anote seu número: " +
                createdNumber +
                ". O acesso já foi realizado."
              }
            />
          </div>
        )}
        {error && (
          <div className="welcome-message" role="alert">
            <Message severity="error" text={error} />
          </div>
        )}
        <div className="account-grid">
          <section className="balance-card" aria-label="Saldo da conta">
            <div className="balance-top">
              <span>Saldo disponível</span>
              <i className="pi pi-wallet" />
            </div>
            <div className="balance-value" aria-live="polite">
               {balance ? (
                money(balance.saldo)
              ) : (
                <Skeleton width="75%" height="3rem" />
              )}
            </div>
            <div className="balance-bottom">
              <span>
                Conta <strong>{"—"}</strong>
              </span>
              <span>BRL · Real brasileiro</span>
            </div>
          </section>
          <section className="actions-card">
            <div className="eyebrow">MOVIMENTE SUA CONTA</div>
            <h2>O próximo movimento é seu.</h2>
            <p className="muted">
              Deposite ou saque com o saldo atualizado após cada operação.
            </p>
            <div className="account-actions">
              <Button
                label="Depositar"
                icon="pi pi-plus"
                onClick={() => setKind("depositos")}
                disabled={!balance}
              />
              <Button
                label="Sacar"
                icon="pi pi-minus"
                onClick={() => setKind("saques")}
                outlined
                disabled={!balance}
              />
            </div>
          </section>
        </div>
        <section className="statement-card" aria-labelledby="statement-title">
          <div className="statement-heading">
            <div>
              <h2 id="statement-title">Extrato da conta</h2>
            </div>
          </div>
          <div className="statement-foot">
            <i className="pi pi-sync" />
            <span>
              . Atualização automática a cada 30 segundos.
            </span>
          </div>
        </section>
        <footer className="dashboard-footer">
          <span>Conta remunerada</span>
        </footer>
      </main>
      {kind && (
        <MovementDialog
          key={kind}
          kind={kind}
          session={session}
          onClose={() => setKind(null)}
          onSuccess={success}
        />
      )}
    </>
  );
}
