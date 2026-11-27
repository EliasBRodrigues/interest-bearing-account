import { useRef, useState, useCallback, useEffect } from "react";
import { Button } from "primereact/button";
import { Message } from "primereact/message";
import { Toast } from "primereact/toast";
import { Skeleton } from "primereact/skeleton";
import { DataTable } from "primereact/datatable";
import { Tag } from "primereact/tag";
import { Column } from "primereact/column";
import { api } from "../../utils/api";
import { formatMoney as money } from "../../utils/format";
import MovementDialog from "../screens/MovementDialog";
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
              <p className="muted">
                Cada depósito, saque e rendimento em um só lugar.
              </p>
            </div>
            <span className="entry-count">
              {statement.totalElements} lançamento
              {statement.totalElements === 1 ? "" : "s"}
            </span>
          </div>
          <DataTable
            value={statement.content}
            dataKey="id"
            lazy
            paginator
            rows={size}
            first={page * size}
            totalRecords={statement.totalElements}
            rowsPerPageOptions={[10, 20, 50, 100]}
            onPage={(e) => {
              setPage(e.page);
              setSize(e.rows);
            }}
            loading={loading}
            paginatorTemplate="PrevPageLink PageLinks NextPageLink RowsPerPageDropdown"
            emptyMessage={
              <div className="empty-state">
                <i className="pi pi-inbox" />
                <h3>Seu extrato começa aqui</h3>
                <p>Faça seu primeiro depósito para começar.</p>
              </div>
            }
            tableStyle={{ minWidth: "32rem" }}
          >
            <Column
              field="tipo"
              header="Movimentação"
              body={(row) => {
                const type = types[row.tipo] || types.DEPOSITO;
                return (
                  <div className="entry-type">
                    <span className={"entry-icon " + row.tipo.toLowerCase()}>
                      <i className={"pi " + type.icon} />
                    </span>
                    <Tag value={type.label} severity={type.severity} />
                  </div>
                );
              }}
            />
            <Column
              field="dataHora"
              header="Data e hora"
              body={(row) => (
                <span className="entry-date">{date(row.dataHora)}</span>
              )}
            />
            <Column
              field="valor"
              header="Valor"
              align="right"
              body={(row) => (
                <span
                  className={
                    "entry-value " + (row.tipo === "SAQUE" ? "debit" : "credit")
                  }
                >
                  {row.tipo === "SAQUE" ? "" : "+ "}
                  {money(row.valor)}
                </span>
              )}
            />
          </DataTable>
           <div className="statement-foot">
            <i className="pi pi-sync" />
            <span>
              {updated
                ? "Atualizado às " +
                  updated.toLocaleTimeString("pt-BR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })
                : "Carregando dados da conta"}
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
